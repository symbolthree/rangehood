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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/ProcedureParser.java $
 * $Author: Christopher Ho $
 * $Date: 28/09/10 12:59p $
 * $Revision: 4 $
 *****************************************************************************/



package symplik.oracle.doc.parser;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomDriver;

import symplik.oracle.doc.DBConnection;
import symplik.oracle.doc.Logger;
import symplik.oracle.doc.object.Argument;
import symplik.oracle.doc.object.Comment;
import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.object.Procedure;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;

public class ProcedureParser extends ObjectParser {
  private ArrayList<String> allLines;
  private Procedure         procObj;

  @Override
  public DBObject getODBjectInfo(DBObject dbObj, StringBuffer sb) {
    allLines = super.convertToLines(sb);
    procObj  = (Procedure) dbObj;

    Comment comment = super.getComment(allLines);

    procObj.setComment(comment);
    this.getArgs();

    return procObj;
  }

  private void getArgs() {
    try {
      Connection conn = DBConnection.getInstance().getConnection();
      String     sql  = "SELECT B.ARGUMENT_NAME" 
                      + "     , DECODE(B.DATA_TYPE, 'PL/SQL BOOLEAN', 'BOOLEAN', B.DATA_TYPE) DATA_TYPE" 
                      + "     , B.DEFAULT_VALUE"
                      + "     , B.POSITION" 
                      + "     , B.IN_OUT" 
                      + "  FROM USER_OBJECTS A" 
                      + "     , USER_ARGUMENTS B"
                      + " WHERE A.OBJECT_NAME=?" 
                      + "   AND A.OBJECT_TYPE='PROCEDURE'"
                      + "   AND A.OBJECT_ID=B.OBJECT_ID" 
                      + "  ORDER BY SEQUENCE";
      
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, procObj.getName());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        Argument arg = new Argument();

        arg.setName(rs.getString(1));
        arg.setType(rs.getString(2));
        arg.setDefaultValue(rs.getString(3));
        arg.setPosition(rs.getInt(4));
        arg.setInout(rs.getString(5));
        procObj.addArgument(arg);
      }

      rs.close();
      ps.close();
    } catch (Exception e) {
      Logger.logError(e);
    }
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream(new JDomDriver());

    try {
      xstream.alias("DBObject", Procedure.class);
      xstream.useAttributeFor(DBObject.class, "name");
      xstream.useAttributeFor(DBObject.class, "schema");
      xstream.useAttributeFor(DBObject.class, "objectType");
      xstream.alias("argument", Argument.class);
      xstream.useAttributeFor(Argument.class, "name");
      xstream.useAttributeFor(Argument.class, "inout");
      xstream.useAttributeFor(Argument.class, "type");
      xstream.useAttributeFor(Argument.class, "origType");
      xstream.useAttributeFor(Argument.class, "position");
      xstream.useAttributeFor(Argument.class, "defaultValue");

      String xmlString = xstream.toXML(procObj);

      super.writeXMLOutput(procObj, xmlString);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
