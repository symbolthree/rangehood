/******************************************************************************
 *
 * SYMPLiK RANGEHOOD
 * Copyright (C) 2010 Christopher Ho / SYMPLiK Tech. Co. Ltd.
 * All Rights Reserved, http://www.symplik.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: christopher.ho@symplik.com
 *
 * ================================================
 *
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/PackageParser.java $
 * $Author: Christopher Ho $
 * $Date: 26/09/10 2:13p $
 * $Revision: 4 $
 *****************************************************************************/



package symplik.oracle.doc.parser;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomDriver;

import symplik.oracle.doc.Logger;
import symplik.oracle.doc.DBConnection;
import symplik.oracle.doc.object.Argument;
import symplik.oracle.doc.object.Column;
import symplik.oracle.doc.object.Comment;
import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.object.Package;
import symplik.oracle.doc.object.SubProgram;
import symplik.oracle.doc.object.Tag;
import symplik.oracle.doc.object.SubType;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Iterator;

public class PackageParser extends ObjectParser {
  Package                   packageObj;
  private ArrayList<String> allLines;

//private StringBuffer source;

  @Override
  public DBObject getODBjectInfo(DBObject dbObj, StringBuffer sb) {
    packageObj = (Package) dbObj;
    allLines   = super.convertToLines(sb);

    try {
      Comment comment = super.getComment(allLines);

      packageObj.setComment(comment);
      this.getPackageObjectID();
      this.getSubPrograms();
      this.getSubProgramComment();
      this.getArgumentDefault();
      this.getAllTypes();
      this.getTypeDefault();
    } catch (Exception e) {
      Logger.log(LOG_ERROR, "Error found for package " + packageObj.getName());
      Logger.logError(e);
    }

    return packageObj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream(new JDomDriver());

    try {
      xstream.alias("DBObject", Package.class);
      xstream.useAttributeFor(DBObject.class, "name");
      xstream.useAttributeFor(DBObject.class, "schema");
      xstream.useAttributeFor(DBObject.class, "objectType");
      xstream.useAttributeFor(DBObject.class, "objectID");
      xstream.alias("tag", Tag.class);
      xstream.useAttributeFor(Tag.class, "name");
      xstream.useAttributeFor(Tag.class, "value");
      xstream.useAttributeFor(Tag.class, "comment");
      xstream.alias("subProgram", SubProgram.class);
      xstream.useAttributeFor(SubProgram.class, "subprogramID");
      xstream.useAttributeFor(SubProgram.class, "overload");
      xstream.alias("argument", Argument.class);
      xstream.useAttributeFor(Argument.class, "name");
      xstream.useAttributeFor(Argument.class, "inout");
      xstream.useAttributeFor(Argument.class, "type");
      xstream.useAttributeFor(Argument.class, "origType");
      xstream.useAttributeFor(Argument.class, "position");
      xstream.useAttributeFor(Argument.class, "defaultValue");
      xstream.alias("type", SubType.class);
      xstream.alias("column", Column.class);
      xstream.useAttributeFor(Column.class, "name");
      xstream.useAttributeFor(Column.class, "type");
      xstream.useAttributeFor(Column.class, "columnID");
      xstream.useAttributeFor(Column.class, "length");
      xstream.useAttributeFor(Column.class, "defaultValue");

      String xmlString = xstream.toXML(packageObj);

      super.writeXMLOutput(packageObj, xmlString);
    } catch (Exception e) {
      Logger.logError(e);
    }
  }

  private void getPackageObjectID() {
    try {
      String            sql  = "SELECT OBJECT_ID FROM USER_OBJECTS WHERE OBJECT_NAME=? AND OBJECT_TYPE='PACKAGE'";
      Connection        conn = DBConnection.getInstance().getConnection();
      PreparedStatement ps   = conn.prepareStatement(sql);

      ps.setString(1, packageObj.getName());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        packageObj.setObjectID(rs.getInt(1));
      }

      rs.close();
      ps.close();
    } catch (Exception e) {
      Logger.logError(e);
    }
  }

  private void getSubPrograms() {

    // get all subprogram in packages
    try {
      Connection conn = DBConnection.getInstance().getConnection();
      String     sql  = "SELECT b.OBJECT_ID" 
                      + "     , b.SUBPROGRAM_ID" 
                      + "     , b.PROCEDURE_NAME"
                      + "     , b.OVERLOAD" 
                      + "  FROM user_objects a" 
                      + "     , user_procedures b"
                      + " WHERE a.object_name = ?"
                      + "   AND a.object_type = 'PACKAGE'"
                      + "   AND a.object_id = b.object_id" 
                      + "   AND b.subprogram_id > 0" 
                      + " ORDER BY SUBPROGRAM_ID";
      
      String sql2 = 
          "    SELECT ARGUMENT_NAME" 
        + "         , POSITION"
        + "         , DECODE(DATA_TYPE, 'PL/SQL BOOLEAN', 'BOOLEAN', DATA_TYPE) ORIG_DATA_TYPE"
        + "         , DECODE(DATA_TYPE, 'PL/SQL BOOLEAN', 'BOOLEAN',"
        + "                  'PL/SQL TABLE', DECODE(TYPE_NAME, PACKAGE_NAME, NULL, TYPE_NAME || '.') || TYPE_SUBNAME,"
        + "                  'PL/SQL RECORD', DECODE(TYPE_NAME, PACKAGE_NAME, NULL, TYPE_NAME || '.') ||  TYPE_SUBNAME,"
        + "                  DATA_TYPE) DATA_TYPE , IN_OUT "
        +	"      FROM USER_ARGUMENTS"
        + "     WHERE OBJECT_ID=?  " 
        +	"       AND SUBPROGRAM_ID=? " 
        + "       AND SEQUENCE > 0" 
        + "       AND DATA_LEVEL=0" 
        + "       AND NVL(OVERLOAD,-1) = ?"
        + "     ORDER BY SEQUENCE";
      
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, packageObj.getName());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        SubProgram subProg = new SubProgram();

        subProg.setObjectID(rs.getInt(1));
        subProg.setSubprogramID(rs.getInt(2));
        subProg.setName(rs.getString(3));
        subProg.setOverload(rs.getInt(4));

        PreparedStatement ps2 = conn.prepareStatement(sql2);

        ps2.setInt(1, subProg.getObjectID());
        ps2.setInt(2, subProg.getSubprogramID());
        ps2.setInt(3, subProg.getOverload());

        ResultSet rs2 = ps2.executeQuery();

        while (rs2.next()) {
          Argument arg = new Argument();

          arg.setName(rs2.getString(1));
          arg.setPosition(rs2.getInt(2));
          arg.setOrigType(rs2.getString(3));
          arg.setType(rs2.getString(4));
          arg.setInout(rs2.getString(5));
          subProg.addArg(arg);

          // detect whether it is a function or procedure
          if (subProg.getObjectType() == null) {
            if ((arg.getName() == null) && arg.getInout().equals("OUT")) {
              subProg.setObjectType("FUNCTION");
            } else {
              subProg.setObjectType("PROCEDURE");
            }
          }
        }

        rs2.close();
        ps2.close();

        // if no argument it is a procedure
        if (subProg.getObjectType() == null) {
          subProg.setObjectType("PROCEDURE");
        }

        packageObj.addSubProgram(subProg);
      }

      rs.close();
      ps.close();
    } catch (Exception e) {
      Logger.logError(e);
    }
  }

  private void getAllTypes() {
    try {
      String sql1 =   "select distinct data_type" 
                    + "     , type_subname" 
                    + "  from user_arguments"
                    + " where package_name=type_name" 
                    + "   and object_id=?"
                    + " order by 1,2";
      Logger.log(LOG_DEBUG, sql1);
      
      String sql2 = "SELECT position" 
                  + "     , sequence" 
                  + "     , subprogram_id" 
                  + "     , data_level"
                  + "  FROM user_arguments" 
                  + " WHERE type_subname = ?" 
                  + "   AND object_id = ?"
                  + "   AND ROWNUM < 2";
      Logger.log(LOG_DEBUG, sql2);      
      
      String sql3 = "SELECT position" 
                  + "     , sequence" 
                  + "     , argument_name" 
                  + "     , decode(data_type,'PL/SQL BOOLEAN','BOOLEAN', data_type) data_type"
                  + "     , data_length" 
                  + "     , data_level" 
                  + "  FROM user_arguments" 
                  + " WHERE object_id = ?"
                  + "   AND subprogram_id = ?" 
                  + "   AND sequence > ?" 
                  + " ORDER BY sequence";
      Logger.log(LOG_DEBUG, sql3);
      
      String sql4 = "SELECT TYPE_SUBNAME" 
                  + "  FROM USER_ARGUMENTS" 
                  + " WHERE OBJECT_ID=? " 
                  + "   AND SUBPROGRAM_ID=?"
                  + "   AND SEQUENCE=?";
      Logger.log(LOG_DEBUG, sql4);
      
      Connection        conn = DBConnection.getInstance().getConnection();
      PreparedStatement ps   = conn.prepareStatement(sql1);

      ps.setInt(1, packageObj.getObjectID());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        SubType type = new SubType();

        type.setObjectType(rs.getString(1));
        type.setName(rs.getString(2));
        packageObj.addType(type);
      }

      rs.close();
      ps.close();

      Iterator<SubType> typesItr = packageObj.getTypes().iterator();

      while (typesItr.hasNext()) {
        SubType type = typesItr.next();

        if (type.getObjectType().equals("PL/SQL RECORD")) {
          PreparedStatement ps2 = conn.prepareStatement(sql2);
          ps2.setString(1, type.getName());
          ps2.setInt(2, packageObj.getObjectID());

          ResultSet rs2 = ps2.executeQuery();

          while (rs2.next()) {

//          int position = rs2.getInt(1);
            int sequence        = rs2.getInt(2);
            int subprogramID    = rs2.getInt(3);
            int recordDataLevel = rs2.getInt(4) + 1;

            PreparedStatement ps3 = conn.prepareStatement(sql3);
            ps3.setInt(1, packageObj.getObjectID());
            ps3.setInt(2, subprogramID);
            ps3.setInt(3, sequence);

            ResultSet rs3 = ps3.executeQuery();

            while (rs3.next()) {
              int dataLevel = rs3.getInt(6);

              // if (rs3.getInt(1) == position+1 && rs3.getInt(6) == 0) break;
              if (dataLevel != recordDataLevel) {
                break;
              }

              Column column = new Column();

              column.setName(rs3.getString(3));
              column.setType(rs3.getString(4));
              column.setLength(rs3.getInt(5));
              type.addColumn(column);
            }
            rs3.close();
            ps3.close();
          }

          rs2.close();
          ps2.close();
          
          // end if for PL/SQL RECORD
        } else if (type.getObjectType().equals("PL/SQL TABLE")) {
          PreparedStatement ps2 = conn.prepareStatement(sql2);
          ps2.setString(1, type.getName());
          ps2.setInt(2, packageObj.getObjectID());

          ResultSet rs2 = ps2.executeQuery();

          while (rs2.next()) {
            int sequence     = rs2.getInt(2) + 1;
            int subprogramID = rs2.getInt(3);

            PreparedStatement ps4 = conn.prepareStatement(sql4);            
            ps4.setInt(1, packageObj.getObjectID());
            ps4.setInt(2, subprogramID);
            ps4.setInt(3, sequence);

            ResultSet rs4 = ps4.executeQuery();

            while (rs4.next()) {
              Column column = new Column();

              column.setName(rs4.getString(1));
              column.setType("PL/SQL RECORD");
              type.addColumn(column);
            }

            rs4.close();
            ps4.close();
            
          }

          rs2.close();
          ps2.close();
        }    // end if for PL/SQL TABLE
      }
    } catch (Exception e) {
      Logger.logError(e);
    }
  }

  private void getTypeDefault() {

    // int lineNo = 0;
    int            start = 0;
    Iterator<SubType> itr   = packageObj.getTypes().iterator();

    while (itr.hasNext()) {
      int  lineNo = 0;
      SubType type   = itr.next();

      if (type.getObjectType().equals("PL/SQL RECORD")) {
        String typeName = type.getName();

        Logger.log(LOG_DEBUG, "Type Name = " + typeName);

        // check first line no for the subprogram
        while (lineNo < allLines.size()) {
          String line = allLines.get(lineNo).trim().toUpperCase();

          if (line.startsWith("TYPE " + typeName)) {
            lineNo++;
            start = lineNo;
            Logger.log(LOG_DEBUG, "TYPE start at line " + start);

            break;
          } else {
            lineNo++;
          }
        }

        Iterator<Column> itrCols = type.getColumns().iterator();

        while (itrCols.hasNext()) {
          Column col = itrCols.next();

          Logger.log(LOG_DEBUG, "Checking " + col.getName());

          while (true) {
            String line = allLines.get(start).trim();

            if (line.toUpperCase().startsWith(col.getName())) {
              Logger.log(LOG_DEBUG, start + "|" + line);

              int delimiterPos = -1;
              int delimiterLen = -1;

              delimiterPos = line.indexOf("DEFAULT");
              delimiterLen = 7;

              if (delimiterPos < 0) {
                delimiterPos = line.indexOf(":=");
                delimiterLen = 2;
              }

              if (delimiterPos < 0) {
                start++;
                Logger.log(LOG_DEBUG, "No default value");

                break;
              }

              int endPos = -1;

              endPos = line.indexOf(",");

              if (endPos < 0) {
                endPos = line.indexOf(")");
              }

              if ((delimiterPos + delimiterLen > endPos) || (endPos < 0)) {
                endPos = line.length();
              }

              // System.out.println(delimiterPos + "-" + delimiterLen + "-" + endPos);
              String defaultValue = line.substring(delimiterPos + delimiterLen, endPos);

              if (defaultValue.indexOf("--") > 0) {
                defaultValue = defaultValue.substring(0, defaultValue.indexOf("--"));
              }

              if (defaultValue.endsWith(");")) {
                defaultValue = defaultValue.substring(0, defaultValue.length() - 2);
              }

              col.setDefaultValue(defaultValue.trim());
              Logger.log(LOG_DEBUG, "Set Default value:" + defaultValue);
              start++;

              break;
            } else {
              start++;
            }
          }
        }
      }
    }
  }

  private void getArgumentDefault() {
    int                  lineNo = 0;
    int                  start  = 0;
    Iterator<SubProgram> itr    = packageObj.getSubPrograms().iterator();

    while (itr.hasNext()) {
      SubProgram          sp       = itr.next();

      // System.out.println(sp.getName());
      ArrayList<Argument> listArgs = sp.getArgs();

      // check first line no for the subprogram
      while (lineNo < allLines.size()) {
        String line = allLines.get(lineNo);

        if (line.startsWith(sp.getObjectType() + " " + sp.getName())) {
          lineNo++;
          start = lineNo;

          break;
        } else {
          lineNo++;
        }
      }

      // System.out.println(sp.getName() + " start at " + start);

      // scan arguments
      for (int argNo = 0; argNo < listArgs.size(); argNo++) {
        Argument arg     = listArgs.get(argNo);
        String   argName = arg.getName();

        // System.out.println("check " + argNo + ":" + argName);
        if ((argName != null) ||!arg.getInout().equals("OUT")) {

          // scan lines
          while (true) {
            String line = allLines.get(start).trim();

            if (line.toUpperCase().startsWith(argName)) {

              // System.out.println(start + "|" + line);
              int delimiterPos = -1;
              int delimiterLen = -1;

              delimiterPos = line.indexOf("DEFAULT");
              delimiterLen = 7;

              if (delimiterPos < 0) {
                delimiterPos = line.indexOf(":=");
                delimiterLen = 2;
              }

              if (delimiterPos < 0) {
                start++;

                // System.out.println("No default value");
                break;
              }

              int endPos = -1;

              endPos = line.indexOf(",");

              if (endPos < 0) {
                endPos = line.indexOf(")");
              }

              if (endPos < 0) {
                endPos = line.length();
              }

              String defaultValue = line.substring(delimiterPos + delimiterLen, endPos);

              arg.setDefaultValue(defaultValue.trim());

              // System.out.println("Set Default value:" + defaultValue);
              start++;

              break;
            } else {
              start++;
            }
          }
        } else {

          // System.out.println("No default: return value");
        }
      }
    }
  }

  private void getSubProgramComment() {
    Iterator<SubProgram> itr        = packageObj.getSubPrograms().iterator();
    int                  lineNo     = 0;
    int                  lastLineNo = 0;

    while (itr.hasNext()) {
      SubProgram subprog  = itr.next();
      String     progName = subprog.getName();
      String     progType = subprog.getObjectType();

      // scan for program line
      lastLineNo = lineNo;

      for (int i = lastLineNo; i < allLines.size(); i++) {
        String line = allLines.get(i).toUpperCase();

        if (line.startsWith(progType) && (line.indexOf(progName) > 0)) {
          lineNo = i;

          break;
        }
      }

      if (lineNo > lastLineNo) {
        int end   = lineNo - 1;
        int start = end;

        for (int i = end; i > 0; i--) {
          if (allLines.get(i).startsWith("*") || allLines.get(i).endsWith("*/") || allLines.get(i).startsWith("/*")) {
            start = i;

            if (allLines.get(i).startsWith("/*")) {
              break;
            }
          } else {
            break;
          }
        }

        if (end > start) {
          ArrayList<String> commentStrArry = new ArrayList<String>();

          for (int i = start; i <= end; i++) {
            commentStrArry.add(allLines.get(i));
          }

          Comment comment = super.getComment(commentStrArry);

          subprog.setComment(comment);
        }
      }    // comment found
    }      // end subprogram while loop
  }
  
  
  private void getTypeDefinition() {
    
  }
}
