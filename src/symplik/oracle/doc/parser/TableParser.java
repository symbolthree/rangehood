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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/TableParser.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:36p $
 * $Revision: 4 $
 *****************************************************************************/

package symplik.oracle.doc.parser;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomDriver;

import symplik.oracle.doc.Logger;
import symplik.oracle.doc.DBConnection;
import symplik.oracle.doc.object.Column;
import symplik.oracle.doc.object.Comment;
import symplik.oracle.doc.object.Constraint;
import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.object.Index;
import symplik.oracle.doc.object.Table;
import symplik.oracle.doc.object.Trigger;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
                 + "  FROM USER_TAB_COMMENTS A, USER_OBJECTS B"
                 + " WHERE A.TABLE_NAME = ?" 
                 + "   AND A.TABLE_NAME = B.OBJECT_NAME"
                 + "   AND B.OBJECT_TYPE = 'TABLE'";
      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, obj.getName());

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
                  + "  FROM USER_TAB_COLUMNS A" 
                  + "     , USER_COL_COMMENTS B" 
                  + " WHERE A.TABLE_NAME = ?"
                  + "   AND A.TABLE_NAME = B.TABLE_NAME" 
                  + "   AND A.COLUMN_NAME = B.COLUMN_NAME"
                  + " ORDER BY A.COLUMN_ID";

      ps = conn.prepareStatement(sql2);
      ps.setString(1, tableObj.getName());
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
                  + "  FROM USER_INDEXES"
                  + " WHERE TABLE_NAME=?";
      
      String sql4 = "SELECT A.COLUMN_NAME" 
                  + "     , B.COLUMN_EXPRESSION" 
                  + "  FROM all_ind_columns A"
                  + "     , all_ind_expressions B" 
                  + " where A.index_name=?" 
                  + "   and A.index_name=B.index_name(+)";

      ps = conn.prepareStatement(sql3);

      PreparedStatement ps2 = conn.prepareStatement(sql4);

      ps.setString(1, tableObj.getName());
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
        tableObj.addIndex(index);
      }

      rs.close();
      ps.close();
      ps2.close();

      /* Constraints */
      String sql5 = "select constraint_name" 
                  + "     , constraint_type" 
                  + "     , search_condition"
                  + "     , r_constraint_name" 
                  + "     , decode(constraint_type, 'P','1','R','2','3') sortOrder"
                  + "  from user_constraints a" 
                  + " where table_name=?" + " order by 5, 1";
      
      String sql6 = "select column_name from user_cons_columns where constraint_name=? order by position";
      String sql7 = "select table_name, column_name from USER_CONS_COLUMNS where constraint_name=? order by position";

      ps = conn.prepareStatement(sql5);

      PreparedStatement ps3 = conn.prepareStatement(sql6);

      ps.setString(1, tableObj.getName());
      rs = ps.executeQuery();

      while (rs.next()) {
        Constraint con = new Constraint();

        con.setName(rs.getString(1));
        con.setType(rs.getString(2));
        con.setDetails(rs.getString(3));
        con.setRname(rs.getString(4));
        
        ps3.setString(1, con.getName());

        ResultSet rs3 = ps3.executeQuery();

        while (rs3.next()) {
          con.addColumn(rs3.getString(1));
        }

        rs3.close();

        // foriegn key
        if (con.getType().equals("R")) {
          PreparedStatement ps4 = conn.prepareStatement(sql7);          
          ps4.setString(1, con.getRname());

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
      String sql8 = "SELECT owner" 
                  + "     , trigger_name" 
                  + "     , trigger_type" 
                  + "     , triggering_event"
                  + "  FROM all_triggers" 
                  + " WHERE table_name = ?" 
                  + "   AND table_owner = ?"
                  + " ORDER BY trigger_name";
      
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
      Logger.logError(e);
    }

    return obj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream(new JDomDriver());

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
      Logger.logError(e);
    }
  }
}
