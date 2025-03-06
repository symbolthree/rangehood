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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/TriggerParser.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:36p $
 * $Revision: 3 $
 *****************************************************************************/



package symplik.oracle.doc.parser;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomDriver;

import symplik.oracle.doc.Logger;
import symplik.oracle.doc.DBConnection;
import symplik.oracle.doc.object.Comment;
import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.object.Trigger;

//~--- JDK imports ------------------------------------------------------------

import java.io.InputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;

public class TriggerParser extends ObjectParser {
  private Trigger triObj;

  @Override
  public DBObject getODBjectInfo(DBObject dbObj, StringBuffer sb) {
    Connection conn;

    triObj = (Trigger) dbObj;

    try {
      conn = DBConnection.getInstance().getConnection();

      String sql = "SELECT TRIGGER_TYPE" 
                 + "     , TRIGGERING_EVENT" 
                 + "     , TABLE_OWNER" 
                 + "     , BASE_OBJECT_TYPE"
                 + "     , TABLE_NAME" 
                 + "     , WHEN_CLAUSE" 
                 + "     , REFERENCING_NAMES" 
                 + "     , TRIGGER_BODY"
                 + " FROM USER_TRIGGERS WHERE TRIGGER_NAME=?";
      
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, triObj.getName());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        ArrayList<String> allLines = super.convertToLines(sb);
        Comment           comment  = super.getComment(allLines);

        triObj.setComment(comment);
        triObj.setType(rs.getString(1));
        triObj.setEvent(rs.getString(2));
        triObj.setTableOwner(rs.getString(3));
        triObj.setBaseObjectType(rs.getString(4));
        triObj.setTableName(rs.getString(5));
        triObj.setWhenClause(rs.getString(6));
        triObj.setReferencingNames(rs.getString(7));

        // long datatype
        int          chunk;
        StringBuffer triggerBody = new StringBuffer();
        InputStream  is          = rs.getAsciiStream(8);

        while ((chunk = is.read()) != -1) {
          triggerBody.append((char) chunk);
        }

        triObj.setBody(stripNonValidXMLCharacters(triggerBody.toString()));
      }
    } catch (Exception e) {
      Logger.logError(e);
    }

    return triObj;
  }

  @Override
  public void serializer() {
    String  xmlString = null;
    XStream xstream   = new XStream(new JDomDriver());

    try {
      xstream.alias("DBObject", Trigger.class);
      xstream.useAttributeFor(DBObject.class, "name");
      xstream.useAttributeFor(DBObject.class, "schema");
      xstream.useAttributeFor(DBObject.class, "objectType");
      xmlString = xstream.toXML(triObj);
      super.writeXMLOutput(triObj, xmlString);
    } catch (Exception e) {
      Logger.logError(e);
    }
  }
}
