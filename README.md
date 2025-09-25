# Rangehood 2.0


## Introduction
- **Ranghhood** is an Oracle DB objects documentation tool which extracts the definitions of a user-specific set of database object, and present these definitions in HTML format. 
- The output format of this program is very similar to **Javadoc**, which is the *de-facto* documentation of Java programing language.  

## Features
- Open source, pure-Java, platform-independent, stand-alone, self-contained, simple and quick.  
- Oracle DB Objects supported:
  - Fnunction
  - Materialized View
  - Table
  - Procedure
  - Pacakge
  - Sequence
  - Trigger
  - Type
  - View
- Ouput HTML can be easily customized to fit your company's style
- CLI friendly (it's actually a CLI program)

- **This program requires Java 11 or higher**


## Quick Start
- Download the release archive from Gitlab.  Unzip it to any directory.
- Open the file `RANGEHOOD.XML`.  Change the parmeters `URL`, `USERNAME` and `PASSWORD` for your environment. 
```
<RANGEHOOD>
	<PROFILE name="DEFAULT" output="OUTPUT" template="FANCY" append="N">
		<DATABASE>
			<URL>jdbc:oracle:thin:@localhost:1521:DB</URL>
			<USERNAME>SYS AS SYSDBA</USERNAME>
			<PASSWORD>manager</PASSWORD>
		</DATABASE>
	    <TITLE>Oracle DBMS XML API</TITLE>
		<DBOBJECT owner="XDB" include="Y" type="ALL" name="DBMS_XML%"/>
		<DBOBJECT owner="SYS" include="Y" type="ALL" name="DBMS_XML%"/>
	</PROFILE>
</RANGEHOOD>
```
- Run `RANGEHOOD.bat | .sh` 
- A new folder `OUTPUT` is created.  Open `INDEX.HTML` with any browser. The output will be like 
  > https://www.symbolthree.com/static/rangehood/SIMPLE/INDEX.HTML

- If you have an Oracle EBS Application database, you can change the parameters in profile `GL_EXAMPLE`.  The output will be like
  > https://www.symbolthree.com/static/rangehood/FANCY/INDEX.HTML 


## Command Line Syntax Explained
> RANGEHOOD.bat / RANGEHOOD.sh
- Whne it runs without argument, the profile `DEFAULT` is picked up in the `RANGEHOOD.XML` file. Or you can run for a specific profile by `RANGEHOOD.sh -profile [profileName]`

```
usage: RANGEHOOD
 -profile <arg>   profile name in RANGEHOOD.XML. Default profile is DEDAULT
 -keepxml         Keep the data XML files. Default is NO
 -keepxsl         Keep data XSL files. Default is NO
```

- The program generates XML data file for each DB objects. Using the specified template style (`SIMPLE` or `FANCY`), the XSL files of that style for that object type are used to transfrom the XML to HTML. 

- The final data should only contains HTML and gif/css files.  The generated XML and template XSL will be cleanup.

- For debugging purpose, you can add argument `-keepxml` or `-keepxsl` to leave these files in the output folder.

## Customization
- The following files in the template folders contain the variable `TITLE`, which will be substituted by the value specified in the profile `<TITLE>` tag.
  - `INDEX.HTML`
  - `HTML/HEADER.XML`
- Making change of `HTML/css/rangehood.css` to fit your company's style
- You can customize `HTML/INDEX.HTML`, `HTML/HEADER.XML`, `HTML/FOOTER.XML` to fit your needs.

## Files and Directories

| File | Description |
|--|--|
|rangehood-2.0.jar | Program uber jar file |
|RANGEHOOD.bat | Windows batch file |
|RANGEHOOD.sh | Linux shell script |
|RANGEHOOD.XML | Program config file |
|RANGEHOOD.DTD | config file definition |
|log4j2.xml | logging config file |
|template/FANCY | template for fancy style (with icons and colors) |
|template/SIMPLE | template for simple style (pure text) |

## RANGEHOOD.XML Profile Settings
- This is a sample file for generating documentation of `DBMS_XML` package, views amd types
  ```
  <DBOBJECT owner="XDB" include="Y" type="ALL" name="DBMS_XML%"/>
  <DBOBJECT owner="SYS" include="Y" type="ALL" name="DBMS_XML%"/>
  ```

- The SQL for these DBOBJECT tags will become 
  ```
  SELECT A.OBJECT_TYPE, A.OBJECT_NAME, A.OWNER
    FROM ALL_OBJECTS A
  WHERE  A.OBJECT_TYPE LIKE '%'  // *ALL* type
      AND A.OBJECT_NAME LIKE 'DBMS_XML%'
      AND A.OWNER = 'XDB'
  UNION
  SELECT A.OBJECT_TYPE, A.OBJECT_NAME, A.OWNER
    FROM ALL_OBJECTS A
    WHERE     A.OBJECT_TYPE LIKE '%'
          AND A.OBJECT_NAME LIKE 'DBMS_XML%'
          AND A.OWNER = 'SYS'
  ```
- The `type` attribute can only take **ONE** object type at a time. So if you want to include both VIEW and TABLE but not others, you can use make use two `DBOBJECT` tags:
  ```
  <DBOBJECT owner="XDB" include="Y" type="VIEW" name="DBMS_XML%"/>
  <DBOBJECT owner="XDB" include="Y" type="TABLE" name="DBMS_XML%"/>
  ```
- The database credentials is for creation the connection, and the owner of DBOBJECT is not necessarily the same as the connected user.  However you must explicit put the `owner` attribute in order to make the SQL statment complete.  


