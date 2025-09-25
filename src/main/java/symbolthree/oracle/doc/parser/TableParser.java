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
import symbolthree.oracle.doc.object.Constraint;
import symbolthree.oracle.doc.object.DBObject;
import symbolthree.oracle.doc.object.Index;
import symbolthree.oracle.doc.object.Table;
import symbolthree.oracle.doc.object.Trigger;

public class TableParser extends ObjectParser {
  Table tableObj;

  @Override
  public DBObject getODBjectInfo(DBObject obj, StringBuffer sb) {
    tableObj = (Table) obj;

    Connection conn;

    try {
      conn = DBConnection.getInstance().getConnection();

      /* Columns */
      String sql = "SELECT B.OBJECT_ID, A.COMMENTS"
                 + "  FROM ALL_TAB_COMMENTS A, ALL_OBJECTS B"
                 + " WHERE A.TABLE_NAME = ?"
                 + "   AND A.OWNER = ?"                 
                 + "   AND A.OWNER = B.OWNER"
                 + "   AND A.TABLE_NAME = B.OBJECT_NAME"
                 + "   AND B.OBJECT_TYPE = 'TABLE'";
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, obj.getName());
      ps.setString(2, obj.getSchema());

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        tableObj.setObjectID(rs.getInt(1));

        Comment comment = new Comment();

        comment.setContent(rs.getString(2));
        tableObj.setComment(comment);
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
                  + "  FROM ALL_TAB_COLUMNS A"
                  + "     , ALL_COL_COMMENTS B"
                  + " WHERE A.TABLE_NAME = ?"
                  + "   AND A.OWNER = ?"                 
                  + "   AND A.OWNER = B.OWNER"
                  + "   AND A.TABLE_NAME = B.TABLE_NAME"
                  + "   AND A.COLUMN_NAME = B.COLUMN_NAME"
                  + " ORDER BY A.COLUMN_ID";

      ps = conn.prepareStatement(sql2);
      
      ps.setString(1, tableObj.getName());
      ps.setString(2, tableObj.getSchema());
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
        tableObj.addColumn(col);
      }

      rs.close();
      ps.close();

      /* Index */
      String sql3 = "SELECT INDEX_NAME"
                  + "     , INDEX_TYPE"
                  + "     , UNIQUENESS"
                  + "     , OWNER"
                  + "  FROM ALL_INDEXES"
                  + " WHERE TABLE_NAME=?"
                  + "   AND OWNER=?";

      String sql4 = "SELECT A.COLUMN_NAME"
                  + "     , B.COLUMN_EXPRESSION"
                  + "     , A.INDEX_OWNER"
                  + "  FROM ALL_IND_COLUMNS A"
                  + "     , ALL_IND_EXPRESSIONS B"
                  + " WHERE A.INDEX_NAME=?"
                  + "   AND A.TABLE_OWNER=?"
                  + "   AND A.INDEX_NAME=B.INDEX_NAME(+)";

      ps = conn.prepareStatement(sql3);

      PreparedStatement ps2 = conn.prepareStatement(sql4);

      ps.setString(1, tableObj.getName());
      ps.setString(2, tableObj.getSchema());
      
      rs = ps.executeQuery();

      while (rs.next()) {
        Index index = new Index();

        index.setName(rs.getString(1));
        index.setType(rs.getString(2));
        index.setUniqueness(rs.getString(3));
        index.setSchema(rs.getString(4));

        ps2.setString(1, index.getName());
        ps2.setString(2, index.getSchema());

        ResultSet rs2 = ps2.executeQuery();

        while (rs2.next()) {
          if (index.getType().startsWith("FUNCTION")) {
            index.addColumns(rs2.getString(2));
          } else {
            index.addColumns(rs2.getString(1));
          }
        }

        rs2.close();
        tableObj.addIndex(index);
      }

      rs.close();
      ps.close();
      ps2.close();

      /* Constraints */
      String sql5 = "SELECT CONSTRAINT_NAME"
                  + "     , CONSTRAINT_TYPE"
                  + "     , SEARCH_CONDITION"
                  + "     , R_CONSTRAINT_NAME"
                  + "     , OWNER"
                  + "     , decode(CONSTRAINT_TYPE, 'P','1','R','2','3') sortOrder"
                  + "  FROM ALL_CONSTRAINTS a"
                  + " WHERE TABLE_NAME=?" 
                  + "   AND OWNER=?"
                  + " ORDER by 5, 1";

      String sql6 = "select COLUMN_NAME FROM ALL_CONS_COLUMNS WHERE CONSTRAINT_NAME=? AND OWNER=? ORDER BY POSITION";
      String sql7 = "select TABLE_NAME, COLUMN_NAME FROM ALL_CONS_COLUMNS where CONSTRAINT_NAME=? AND OWNER=? ORDER BY POSITION";

      ps = conn.prepareStatement(sql5);

      PreparedStatement ps3 = conn.prepareStatement(sql6);

      ps.setString(1, tableObj.getName());
      ps.setString(2, tableObj.getSchema());
      
      rs = ps.executeQuery();

      while (rs.next()) {
        Constraint con = new Constraint();

        con.setName(rs.getString(1));
        con.setType(rs.getString(2));
        con.setDetails(rs.getString(3));
        con.setRname(rs.getString(4));
        con.setSchema(rs.getString(5));

        ps3.setString(1, con.getName());
        ps3.setString(2, con.getSchema());

        ResultSet rs3 = ps3.executeQuery();

        while (rs3.next()) {
          con.addColumn(rs3.getString(1));
        }

        rs3.close();

        // Foreign key
        if (con.getType().equals("R")) {
          PreparedStatement ps4 = conn.prepareStatement(sql7);
          ps4.setString(1, con.getRname());
          ps4.setString(2, con.getSchema());

          ResultSet rs4 = ps4.executeQuery();

          while (rs4.next()) {
            con.addRcolumn(rs4.getString(1) + "." + rs4.getString(2));
          }
          rs4.close();
          ps4.close();
        }
        tableObj.addConstraint(con);
      }

      rs.close();
      ps3.close();

      // triggers
      String sql8 = "SELECT OWNER"
                  + "     , TRIGGER_NAME"
                  + "     , TRIGGER_TYPE"
                  + "     , TRIGGERING_EVENT"
                  + "  FROM ALL_TRIGGERS"
                  + " WHERE TABLE_NAME = ?"
                  + "   AND TABLE_OWNER = ?"
                  + " ORDER BY TRIGGER_NAME";

      PreparedStatement ps5 = conn.prepareStatement(sql8);

      ps5.setString(1, tableObj.getName());
      ps5.setString(2, tableObj.getSchema());

      ResultSet rs7 = ps5.executeQuery();

      while (rs7.next()) {
        Trigger tri = new Trigger();

        tri.setSchema(rs7.getString(1));
        tri.setName(rs7.getString(2));
        tri.setType(rs7.getString(3));
        tri.setEvent(rs7.getString(4));
        tableObj.addTrigger(tri);
      }

      rs7.close();
      ps5.close();
    } catch (Exception e) {
    	logger.catching(e);
    }

    return obj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream();

    try {
      xstream.alias("DBObject", Table.class);
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
      xstream.alias("constraint", Constraint.class);
      xstream.useAttributeFor(Constraint.class, "name");
      xstream.useAttributeFor(Constraint.class, "type");
      xstream.alias("trigger", Trigger.class);
      xstream.useAttributeFor(Trigger.class, "event");
      xstream.useAttributeFor(Trigger.class, "type");

      String xmlString = xstream.toXML(tableObj);

      super.writeXMLOutput(tableObj, xmlString);
    } catch (Exception e) {
    	logger.catching(e);
    }
  }
}
