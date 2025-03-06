SET DEFINE OFF

CREATE SEQUENCE SYMPLIK_PEOPLE_ALL_SEQ
/

CREATE TABLE SYMPLIK_PEOPLE_ALL (
PERSON_ID         NUMBER PRIMARY KEY,
LAST_NAME         VARCHAR2(50)  NOT NULL,
FIRST_NAME        VARCHAR2(100) NOT NULL,
GENDER            VARCHAR2(1)   CONSTRAINT GENDER_CHK CHECK (GENDER IN ('M', 'F', 'U')),
SSN               VARCHAR2(9),
DATE_OF_BIRTH     DATE,
HEIGHT            NUMBER,
WEIGHT            NUMBER,
CREATION_DATE     DATE,
LAST_UPDATED_DATE DATE
)
/

COMMENT ON TABLE SYMPLIK_PEOPLE_ALL IS 'People Master table for company';
COMMENT ON COLUMN SYMPLIK_PEOPLE_ALL.PERSON_ID IS 'Person ID, Primary Key';
COMMENT ON COLUMN SYMPLIK_PEOPLE_ALL.LAST_NAME IS 'Stored in UPPERCASE';
COMMENT ON COLUMN SYMPLIK_PEOPLE_ALL.FIRST_NAME IS 'Stored in Mix Case';
COMMENT ON COLUMN SYMPLIK_PEOPLE_ALL.GENDER IS 'M, F, or U';
COMMENT ON COLUMN SYMPLIK_PEOPLE_ALL.SSN IS '9-digit social security number';
COMMENT ON COLUMN SYMPLIK_PEOPLE_ALL.HEIGHT IS 'unit in centimeter (cm)';
COMMENT ON COLUMN SYMPLIK_PEOPLE_ALL.WEIGHT IS 'unit in kilogram (kg)';


CREATE TABLE SYMPLIK_EMPLOYMENT (
PERSON_ID            NUMBER REFERENCES SYMPLIK_PEOPLE_ALL(PERSON_ID),
EFFECTIVE_START_DATE DATE,
EFFECTIVE_END_DATE   DATE,
POSITION_ID          NUMBER
)
/
COMMENT ON TABLE SYMPLIK_EMPLOYMENT IS 'Employee employment history';


CREATE TABLE SYMPLIK_DEPARTMENTS (
  DEPARTMENT_ID      NUMBER PRIMARY KEY,
  DEPARTMENT_NAME    VARCHAR2(50),
  ACTIVE             VARCHAR2(1)   NOT NULL CONSTRAINT DEPARTMENT_CHK CHECK (ACTIVE IN ('Y', 'N'))
)
/
COMMENT ON TABLE SYMPLIK_DEPARTMENTS IS 'Department information'
/

CREATE TABLE SYMPLIK_POSITIONS (
  POSITION_ID        NUMBER PRIMARY KEY,
  POSITION_NAME      VARCHAR2(50)  NOT NULL,
  DEPARTMENT_ID      NUMBER REFERENCES SYMPLIK_DEPARTMENTS(DEPARTMENT_ID),
  ACTIVE             VARCHAR2(1)   NOT NULL CONSTRAINT POSITION_CHK CHECK (ACTIVE IN ('Y', 'N')),
  MGR_POSITION_ID    NUMBER REFERENCES SYMPLIK_POSITIONS(POSITION_ID)
)
/
COMMENT ON TABLE SYMPLIK_POSITIONS IS 'Position information including its manager position'
/

CREATE OR REPLACE TRIGGER SYMPLIK_PEOPLE_TRI
/*
* Insert Trigger for table SYMPLIK_PEOPLE_ALL
*
* Auto generate PERSON_ID and creation date.
*
* @author    $Author: Christopher Ho $
* @version   $Header: /TOOL/RANGEHOOD/RANGEHOOD_TEST.SQL 1     28/09/10 11:06a Christopher Ho $
*/
BEFORE INSERT
ON SYMPLIK_PEOPLE_ALL
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
DECLARE
personID NUMBER;
BEGIN
  SELECT  SYMPLIK_PEOPLE_ALL_SEQ.nextval into personID from dual;
  :NEW.LAST_NAME := UPPER(:OLD.LAST_NAME);
  :NEW.LAST_NAME := TRIM(:OLD.LAST_NAME);
  :NEW.PERSON_ID := personID;
  :NEW.CREATION_DATE := SYSDATE;
  :NEW.LAST_UPDATED_DATE := SYSDATE;
END;
/

CREATE OR REPLACE FUNCTION SYMPLIK_GET_MGR_NAME(p_personID IN NUMBER) RETURN VARCHAR2 IS
/*
* Return the manager name of a given person ID
*
* @param   p_personID SYMPLIK_PEOPLE_ALL.PERSON_ID
* @return  manager name in the format of "last name, first_name"
*/
BEGIN
  RETURN NULL;
END;
/

CREATE OR REPLACE FUNCTION SYMPLIK_EMP_AGE (p_personID IN NUMBER, p_format IN VARCHAR2) RETURN NUMBER
IS
/*
* Caculate the total workday of this employee
*
* @param     p_personID    SYMPLIK_PEOPLE_ALL.PERSON_ID
* @param     p_format      return format: DAY (in no. of days), MONTH, YEAR, in 1 decimal place
* @return    Total workday
*
* @author    $Author: Christopher Ho $
* @version   $Header: /TOOL/RANGEHOOD/RANGEHOOD_TEST.SQL 1     28/09/10 11:06a Christopher Ho $
*/
BEGIN
   RETURN NULL;
END SYMPLIK_EMP_AGE;
/


CREATE OR REPLACE PROCEDURE
SYMPLIK_CREATE_EMP_PRC(p_personID IN NUMBER,  p_positionID IN NUMBER, p_startDate IN DATE, p_endDate IN DATE DEFAULT NULL, p_resultCode OUT VARCHAR2) IS
  /*
  * Create a HR employment record for a new person
  *
  * @param     p_personID    SYMPLIK_PEOPLE_ALL.PERSON_ID
  * @param     p_positionID  SYMPLIK_POSITIONS.POSITION_ID
  * @param     p_startDate   new employment start date
  * @param     p_endDate     possible employment end date
  * @param     p_resultCode  'Y' success; 'N' fail
  *
  * @author    $Author: Christopher Ho $
  * @version   $Header: /TOOL/RANGEHOOD/RANGEHOOD_TEST.SQL 1     28/09/10 11:06a Christopher Ho $
  */
BEGIN
  NULL;
END SYMPLIK_CREATE_EMP_PRC;
/

CREATE OR REPLACE PACKAGE SYMPLIK_HR_PKG IS
  /*
  * This package shows standard documentation syntax used for SYMPLIK RANGEHOOD.
  * For PL/SQL package specification, the first comment block below the "CREATE OR REPLACE PACKAGE..."
  * is extracted for PACKAGE comment. For the individual procedure/function, the comment block ABOVE is
  * used. 
  *
  * Similar to Javadoc, the tags can be inserted inside the comment, starting with a @ character.
  * Special tag @param is used for parameters.
  *
  * @author    $Author: Christopher Ho $
  * @version   $Header: /TOOL/RANGEHOOD/RANGEHOOD_TEST.SQL 1     28/09/10 11:06a Christopher Ho $
  */

     /*
     *     Add or update a person profile, based on the SSN as the primary key.
     *     If SSN is already exist, the info is update; otherwise a new person
     *     is added.
     *
     *     @param p_lastName    The last name will be stored in uppercase.
     *     @param p_firstName   The first name will be stored in uppercase.
     *     @param p_gender      Possible choice: M, F, or U (unknown)
     *     @param p_dob         Date of birth
     *     @param p_ssn         Social Security No. All dash or dot are trimmed and must be 9 digit long
     *     @param p_weight      Weight unit in kilogram (kg)
     *     @param p_height      Height unit in centimeter (cm)
     *     @param p_personID    If a new person is added, it will return the new person_ID, otherwise return null.
     */
   PROCEDURE SET_PERSON_PROFILE (p_lastName    IN     VARCHAR2, p_firstName   IN     VARCHAR2,
                                 p_gender      IN     VARCHAR2, p_dob         IN     DATE,
                                 p_ssn         IN     VARCHAR2, p_weight      IN     NUMBER,
                                 p_height      IN     NUMBER,   p_personID    OUT    NUMBER);

   /*
   *   Use SSN to get PERSON_ID.
   *   If no person has found, it return null.
   *
   *   @param   p_SSN     Social Security No.
   *   @return  SYMPLIK_PEOPLE_ALL.PERSON_ID
   */
   FUNCTION GET_PERSON_ID_BY_SSN (p_SSN VARCHAR2) RETURN NUMBER;

   /*
   *   Get the current age of this person.
   *   Age is the different of current year and brith year. If birthday not yet passed, one year younger.
   *
   *   @param   p_personID SYMPLIK_PEOPLE_ALL.PERSON_ID
   *   @return  Age in integer
   */
   FUNCTION GET_PERSON_AGE (p_personID NUMBER) RETURN NUMBER;
END SYMPLIK_HR_PKG;
/

CREATE OR REPLACE VIEW SYMPLIK_CURRENT_EMPOLYEE_V (
	  PERSON_ID
	, LAST_NAME
	, FIRST_NAME
	, DEPARTMENT_NAME
	, POSITION_NAME
	, MANAGER_NAME
) AS
SELECT A.PERSON_ID
     , A.LAST_NAME
	 , A.FIRST_NAME
	 , D.DEPARTMENT_NAME
	 , C.POSITION_NAME
	 , SYMPLIK_GET_MGR_NAME(A.PERSON_ID) MANAGER_NAME
   FROM SYMPLIK_PEOPLE_ALL A
      , SYMPLIK_EMPLOYMENT B
	  , SYMPLIK_POSITIONS C
	  , SYMPLIK_DEPARTMENTS D
  WHERE A.PERSON_ID = B.PERSON_ID
    AND SYSDATE BETWEEN B.EFFECTIVE_START_DATE AND B.EFFECTIVE_END_DATE
	AND B.POSITION_ID=C.POSITION_ID
	AND C.DEPARTMENT_ID=D.DEPARTMENT_ID
   WITH READ ONLY	
/


CREATE MATERIALIZED VIEW SYMPLIK_CURRENT_EMPOLYEE_MV (
	  PERSON_ID
	, LAST_NAME
	, FIRST_NAME
	, DEPARTMENT_NAME
	, POSITION_NAME
	, MANAGER_NAME)
REFRESH FORCE ON DEMAND
WITH PRIMARY KEY
AS   
SELECT A.PERSON_ID
     , A.LAST_NAME
	 , A.FIRST_NAME
	 , D.DEPARTMENT_NAME
	 , C.POSITION_NAME
	 , SYMPLIK_GET_MGR_NAME(A.PERSON_ID) MANAGER_NAME
   FROM SYMPLIK_PEOPLE_ALL A
      , SYMPLIK_EMPLOYMENT B
	  , SYMPLIK_POSITIONS C
	  , SYMPLIK_DEPARTMENTS D
  WHERE A.PERSON_ID = B.PERSON_ID
    AND SYSDATE BETWEEN B.EFFECTIVE_START_DATE AND B.EFFECTIVE_END_DATE
	AND B.POSITION_ID=C.POSITION_ID
	AND C.DEPARTMENT_ID=D.DEPARTMENT_ID
	/

COMMENT ON MATERIALIZED VIEW SYMPLIK_CURRENT_EMPOLYEE_MV IS 'Materialized view of SYMPLIK_CURRENT_EMPOLYEE_V'
/
