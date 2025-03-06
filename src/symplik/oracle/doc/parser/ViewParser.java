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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/ViewParser.java $
 * $Author: Christopher Ho $
 * $Date: 12/10/10 2:25p $
 * $Revision: 5 $
 *****************************************************************************/



package symplik.oracle.doc.parser;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomDriver;

import symplik.oracle.doc.Logger;
import symplik.oracle.doc.DBConnection;
import symplik.oracle.doc.object.Column;
import symplik.oracle.doc.object.Comment;
import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.object.View;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.ArrayList;

public class ViewParser extends ObjectParser {
  View viewObj;

  @Override
  public DBObject getODBjectInfo(DBObject obj, StringBuffer sb) {
    viewObj = (View) obj;
    viewObj.setSource(sb.toString());

    Connection conn;
    String     srcSQL = null;

    Logger.log(LOG_DEBUG, "working with View " + viewObj.getName());
    
    try {
      conn = DBConnection.getInstance().getConnection();

      /* Columns */
      String sql = "SELECT B.OBJECT_ID, A.COMMENTS" 
                 + "  FROM USER_TAB_COMMENTS A, USER_OBJECTS B"
                 + " WHERE A.TABLE_NAME = ?" 
                 + "   AND A.TABLE_NAME = B.OBJECT_NAME"
                 + "   AND B.OBJECT_TYPE = 'VIEW'";
      
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, obj.getName());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        viewObj.setObjectID(rs.getInt(1));

        Comment comment = new Comment();

        comment.setContent(rs.getString(2));
        viewObj.setComment(comment);
      }
      
      rs.close();
      ps.close();
      Logger.log(LOG_DEBUG, "get comment done");      

      String sql2 = "SELECT A.COLUMN_NAME" 
                  + "     , A.DATA_TYPE" 
                  + "     , A.DATA_LENGTH" 
                  + "     , A.NULLABLE"
                  + "     , A.DATA_DEFAULT" 
                  + "     , B.COMMENTS" 
                  + "     , A.COLUMN_ID"
                  + "  FROM USER_TAB_COLUMNS A" 
                  + "     , USER_COL_COMMENTS B" 
                  + " WHERE A.TABLE_NAME = ?"
                  + "   AND A.TABLE_NAME = B.TABLE_NAME" 
                  + "   AND A.COLUMN_NAME = B.COLUMN_NAME"
                  + " ORDER BY A.COLUMN_ID";

      ps = conn.prepareStatement(sql2);
      ps.setString(1, viewObj.getName());
      rs = ps.executeQuery();

      while (rs.next()) {
        Column col = new Column();

        col.setName(rs.getString(1));
        col.setType(rs.getString(2));
        col.setLength(rs.getInt(3));
        col.setNullable(rs.getString(4));
        col.setDefaultValue(rs.getString(5));
        col.setComment(rs.getString(6));
        col.setColumnID(rs.getInt(7));
        viewObj.addColumn(col);
      }

      rs.close();
      ps.close();
      Logger.log(LOG_DEBUG, "add columns done");
      
      // get sourceColumns
      srcSQL = viewObj.getSource();
      
      Logger.log(LOG_DEBUG, srcSQL);
      
      String endStr = srcSQL.substring(srcSQL.length()-14, srcSQL.length());
      Logger.log(LOG_DEBUG, "***" + endStr + "***");       
      if (endStr.toUpperCase().equals("WITH READ ONLY")) {
        srcSQL = srcSQL.substring(0, srcSQL.length() - 14);
      }
      
      srcSQL = srcSQL + " AND ROWNUM < 2";
      
      rs = conn.createStatement().executeQuery(srcSQL);

      ResultSetMetaData metadata = rs.getMetaData();
      ArrayList<Column> cols     = viewObj.getColumns();

      for (int i = 0; i < cols.size(); i++) {
        String sourceCol = metadata.getColumnName(i + 1);

        if (sourceCol.length() > 30) {
          sourceCol = sourceCol.substring(0, 30) + "...";
        }

        cols.get(i).setSourceColumn(sourceCol);
      }
      Logger.log(LOG_DEBUG, "get source columns done");      
      
    } catch (Exception e) {
      Logger.logError(e);
      Logger.log(LOG_ERROR, viewObj.getName());
      Logger.log(LOG_ERROR, srcSQL);
    }

    return viewObj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream(new JDomDriver());

    try {
      xstream.alias("DBObject", View.class);
      xstream.useAttributeFor(DBObject.class, "name");
      xstream.useAttributeFor(DBObject.class, "schema");
      xstream.useAttributeFor(DBObject.class, "objectType");
      xstream.alias("column", Column.class);
      xstream.useAttributeFor(Column.class, "name");
      xstream.useAttributeFor(Column.class, "type");
      xstream.useAttributeFor(Column.class, "nullable");
      xstream.useAttributeFor(Column.class, "columnID");
      xstream.useAttributeFor(Column.class, "length");
      xstream.useAttributeFor(Column.class, "defaultValue");
      xstream.useAttributeFor(Column.class, "sourceColumn");

      String xmlString = xstream.toXML(viewObj);

      super.writeXMLOutput(viewObj, xmlString);
    } catch (Exception e) {
      Logger.logError(e);
    }
  }
}
