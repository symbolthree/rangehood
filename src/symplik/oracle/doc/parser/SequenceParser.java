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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/SequenceParser.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:36p $
 * $Revision: 3 $
 *****************************************************************************/



package symplik.oracle.doc.parser;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomDriver;

import org.jdom.Document;

import symplik.oracle.doc.Logger;
import symplik.oracle.doc.DBConnection;
import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.object.Sequence;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SequenceParser extends ObjectParser {
  Connection conn;
  Document   xmldoc = new Document();
  Sequence   seqObj;

  @Override
  public DBObject getODBjectInfo(DBObject obj, StringBuffer sb) {
    seqObj = (Sequence) obj;

    try {
      conn = DBConnection.getInstance().getConnection();

      String sql = "SELECT A.MIN_VALUE" 
                 + "     , A.MAX_VALUE" 
                 + "     , A.INCREMENT_BY" 
                 + "     , A.CYCLE_FLAG"
                 + "     , A.ORDER_FLAG" 
                 + "     , A.CACHE_SIZE" 
                 + "     , A.LAST_NUMBER" 
                 + "     , B.OBJECT_ID"
                 + "  FROM USER_SEQUENCES A" 
                 + "     , USER_OBJECTS B" 
                 + " WHERE A.SEQUENCE_NAME=B.OBJECT_NAME"
                 + "   AND B.OBJECT_NAME=?";
      
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, seqObj.getName());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        seqObj.setMinValue(rs.getDouble(1));
        seqObj.setMaxValue(rs.getDouble(2));
        seqObj.setIncrementBy(rs.getInt(3));
        seqObj.setCycleFlag(rs.getString(4));
        seqObj.setOrderFlag(rs.getString(5));
        seqObj.setCacheSize(rs.getInt(6));
        seqObj.setLastNumber(rs.getDouble(7));
        seqObj.setObjectID(rs.getInt(8));
      }
    } catch (Exception e) {
      Logger.logError(e);
    }

    return seqObj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream(new JDomDriver());

    try {
      xstream.alias("DBObject", Sequence.class);
      xstream.useAttributeFor(DBObject.class, "name");
      xstream.useAttributeFor(DBObject.class, "schema");
      xstream.useAttributeFor(DBObject.class, "objectType");

      String xmlString = xstream.toXML(seqObj);

      super.writeXMLOutput(seqObj, xmlString);
    } catch (Exception e) {
      Logger.logError(e);
    }
  }
}
