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

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;

import symbolthree.oracle.doc.DBConnection;
import symbolthree.oracle.doc.object.Column;
import symbolthree.oracle.doc.object.Comment;
import symbolthree.oracle.doc.object.DBObject;
import symbolthree.oracle.doc.object.View;

public class ViewParser extends ObjectParser {
  View viewObj;

  @Override
  public DBObject getODBjectInfo(DBObject obj, StringBuffer sb) {
    viewObj = (View) obj;
    viewObj.setSource(sb.toString());

    Connection conn;
    String     srcSQL = null;

    logger.debug("working with View " + viewObj.getName());

    try {
      conn = DBConnection.getInstance().getConnection();

      /* Columns */
      String sql = "SELECT B.OBJECT_ID, A.COMMENTS"
                 + "  FROM ALL_TAB_COMMENTS A, ALL_OBJECTS B"
                 + " WHERE A.TABLE_NAME = ?"
                 + "   AND B.OWNER = ?"
                 + "   AND A.TABLE_NAME = B.OBJECT_NAME"
                 + "   AND B.OBJECT_TYPE = 'VIEW'";

      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, obj.getName());
      ps.setString(2, obj.getSchema());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        viewObj.setObjectID(rs.getInt(1));

        Comment comment = new Comment();

        comment.setContent(rs.getString(2));
        viewObj.setComment(comment);
      }

      rs.close();
      ps.close();
      logger.debug("get comment done");

      String sql2 = "SELECT A.COLUMN_NAME"
                  + "     , A.DATA_TYPE"
                  + "     , A.DATA_LENGTH"
                  + "     , A.NULLABLE"
                  + "     , A.DATA_DEFAULT"
                  + "     , B.COMMENTS"
                  + "     , A.COLUMN_ID"
                  + "  FROM ALL_TAB_COLUMNS A"
                  + "     , ALL_COL_COMMENTS B"
                  + " WHERE A.TABLE_NAME = ?"
                  + "   AND A.OWNER = ?"
                  + "   AND A.OWNER = B.OWNER"
                  + "   AND A.TABLE_NAME = B.TABLE_NAME"
                  + "   AND A.COLUMN_NAME = B.COLUMN_NAME"
                  + " ORDER BY A.COLUMN_ID";

      ps = conn.prepareStatement(sql2);
      
      ps.setString(1, viewObj.getName());
      ps.setString(2, viewObj.getSchema());
      
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
      logger.debug( "add columns done");

      // get sourceColumns
      /*
      srcSQL = viewObj.getSource();

      //Logger.log(LOG_DEBUG, srcSQL);

      String endStr = srcSQL.substring(srcSQL.length()-14, srcSQL.length());
      log(LOG_DEBUG, "***" + endStr + "***");
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
      log(LOG_DEBUG, "get source columns done");
	  */
      
    } catch (Exception e) {
      logger.catching(e);
      logger.error(viewObj.getName());
      logger.error(srcSQL);
    }

    return viewObj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream();

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
    	logger.catching(e);
    }
  }
}
