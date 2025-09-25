/******************************************************************************
 *
 * ≡ RANGEHOOD ≡
 * Copyright (C) 2009-2025 Christopher Ho
 * All Rights Reserved, http://www.symbolthree.com
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
 * E-mail: christopher.ho@symbolthree.com
 *
******************************************************************************/

package symbolthree.oracle.doc.parser;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;

import symbolthree.oracle.doc.DBConnection;
import symbolthree.oracle.doc.object.Argument;
import symbolthree.oracle.doc.object.Comment;
import symbolthree.oracle.doc.object.DBObject;
import symbolthree.oracle.doc.object.Function;

public class FunctionParser extends ObjectParser {
  private ArrayList<String> allLines;
  private Function          funObj;

  @Override
  public DBObject getODBjectInfo(DBObject dbObj, StringBuffer sb) {
    allLines = super.convertToLines(sb);
    funObj   = (Function) dbObj;

    Comment comment = super.getComment(allLines);

    funObj.setComment(comment);
    getArgs();

    return funObj;
  }

  private void getArgs() {
    try {
      Connection conn = DBConnection.getInstance().getConnection();
      String     sql  =   "SELECT B.ARGUMENT_NAME"
                        + "     , DECODE(B.DATA_TYPE, 'PL/SQL BOOLEAN', 'BOOLEAN', B.DATA_TYPE) DATA_TYPE"
                        + "     , B.DEFAULT_VALUE"
                        + "     , B.POSITION"
                        + "     , B.IN_OUT"
                        + "     , B.DEFAULTED"
                        + "  FROM ALL_OBJECTS A"
                        + "     , ALL_ARGUMENTS B"
                        + " WHERE A.OBJECT_NAME=?"
                        + "   AND A.OWNER=?"
                        + "   AND A.OBJECT_TYPE='FUNCTION'"
                        + "   AND A.OBJECT_ID=B.OBJECT_ID"
                        + "  ORDER BY SEQUENCE";
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, funObj.getName());
      ps.setString(2, funObj.getSchema());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        Argument arg = new Argument();

        arg.setName(rs.getString(1));
        arg.setType(rs.getString(2));
        arg.setDefaultValue(rs.getString(3));
        arg.setPosition(rs.getInt(4));
        arg.setInout(rs.getString(5));
        arg.setDefaulted(rs.getString(6));
        funObj.addArgument(arg);
      }

      rs.close();
      ps.close();
    } catch (Exception e) {
      logger.catching(e);
    }
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream();

    try {
      xstream.alias("DBObject", Function.class);
      xstream.useAttributeFor(DBObject.class, "name");
      xstream.useAttributeFor(DBObject.class, "schema");
      xstream.useAttributeFor(DBObject.class, "objectType");
      xstream.alias("argument", Argument.class);
      xstream.useAttributeFor(Argument.class, "name");
      xstream.useAttributeFor(Argument.class, "inout");
      xstream.useAttributeFor(Argument.class, "type");
      xstream.useAttributeFor(Argument.class, "origType");
      xstream.useAttributeFor(Argument.class, "position");
      xstream.useAttributeFor(Argument.class, "defaulted");

      String xmlString = xstream.toXML(funObj);

      super.writeXMLOutput(funObj, xmlString);
    } catch (Exception e) {
    	logger.catching(e);
    }
  }
}
