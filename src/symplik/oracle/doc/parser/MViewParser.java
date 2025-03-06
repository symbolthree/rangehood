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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/MViewParser.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:36p $
 * $Revision: 2 $
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
import symplik.oracle.doc.object.Index;
import symplik.oracle.doc.object.MView;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MViewParser extends ObjectParser {
  MView mviewObj;

  @Override
  public DBObject getODBjectInfo(DBObject obj, StringBuffer sb) {
    mviewObj = (MView) obj;

    Connection conn;

    mviewObj.setSource(sb.toString());

    try {
      conn = DBConnection.getInstance().getConnection();

      /* Columns */
      String sql = "SELECT B.OBJECT_ID, A.COMMENTS" 
                 + "  FROM USER_MVIEW_COMMENTS A, USER_OBJECTS B"
                 + " WHERE A.MVIEW_NAME = ?" 
                 + "   AND A.MVIEW_NAME = B.OBJECT_NAME"
                 + "   AND B.OBJECT_TYPE = 'MATERIALIZED VIEW'";
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, obj.getName());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        mviewObj.setObjectID(rs.getInt(1));

        Comment comment = new Comment();

        comment.setContent(rs.getString(2));
        mviewObj.setComment(comment);
      }

      rs.close();
      ps.close();

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
      ps.setString(1, mviewObj.getName());
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
        mviewObj.addColumn(col);
      }

      rs.close();
      ps.close();

      /* MView-specific parameters */
      sql = "SELECT UPDATABLE" 
          + "     , REWRITE_ENABLED" 
          + "     , REWRITE_CAPABILITY" 
          + "     , REFRESH_MODE"
          + "     , REFRESH_METHOD" 
          + "     , BUILD_MODE" 
          + "     , FAST_REFRESHABLE" 
          + "  FROM USER_MVIEWS"
          + " WHERE MVIEW_NAME=?";
      ps = conn.prepareStatement(sql);
      ps.setString(1, mviewObj.getName());
      rs = ps.executeQuery();

      while (rs.next()) {
        mviewObj.setUpdatable(rs.getString(1));
        mviewObj.setRewriteEnabled(rs.getString(2));
        mviewObj.setRewriteCapability(rs.getString(3));
        mviewObj.setRefreshMode(rs.getString(4));
        mviewObj.setRefreshMethod(rs.getString(5));
        mviewObj.setBuildMode(rs.getString(6));
        mviewObj.setFastRefreshable(rs.getString(6));
      }

      rs.close();
      ps.close();

      /* Index */
      String sql3 = "SELECT INDEX_NAME" 
                  + "     , INDEX_TYPE" 
                  + "     , UNIQUENESS" 
                  + "  FROM USER_INDEXES"
                  + " WHERE TABLE_NAME=?" 
                  + "   AND INDEX_TYPE<> 'FUNCTION-BASED NORMAL'";
      String sql4 = "SELECT A.COLUMN_NAME" 
                  + "     , B.COLUMN_EXPRESSION" 
                  + "  FROM all_ind_columns A"
                  + "     , all_ind_expressions B" 
                  + " where A.index_name=?" 
                  + "   and A.index_name=B.index_name(+)";

      ps = conn.prepareStatement(sql3);

      PreparedStatement ps2 = conn.prepareStatement(sql4);

      ps.setString(1, mviewObj.getName());
      rs = ps.executeQuery();

      while (rs.next()) {
        Index index = new Index();

        index.setName(rs.getString(1));
        index.setType(rs.getString(2));
        index.setUniqueness(rs.getString(3));
        ps2.setString(1, index.getName());

        ResultSet rs2 = ps2.executeQuery();

        while (rs2.next()) {
          if (index.getType().startsWith("FUNCTION")) {
            index.addColumns(rs2.getString(2));
          } else {
            index.addColumns(rs2.getString(1));
          }
        }

        rs2.close();
        mviewObj.addIndex(index);
      }

      rs.close();
      ps.close();
      ps2.close();
    } catch (Exception e) {
      Logger.logError(e);
    }

    return mviewObj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream(new JDomDriver());

    try {
      xstream.alias("DBObject", MView.class);
      xstream.useAttributeFor(DBObject.class, "name");
      xstream.useAttributeFor(DBObject.class, "schema");
      xstream.useAttributeFor(DBObject.class, "objectType");
      xstream.useAttributeFor(DBObject.class, "objectID");
      xstream.alias("column", Column.class);
      xstream.useAttributeFor(Column.class, "name");
      xstream.useAttributeFor(Column.class, "type");
      xstream.useAttributeFor(Column.class, "nullable");
      xstream.useAttributeFor(Column.class, "columnID");
      xstream.useAttributeFor(Column.class, "length");
      xstream.useAttributeFor(Column.class, "defaultValue");
      xstream.alias("index", Index.class);
      xstream.useAttributeFor(Index.class, "name");
      xstream.useAttributeFor(Index.class, "type");
      xstream.useAttributeFor(Index.class, "uniqueness");

      String xmlString = xstream.toXML(mviewObj);

      super.writeXMLOutput(mviewObj, xmlString);
    } catch (Exception e) {
      Logger.logError(e);
    }
  }
}
