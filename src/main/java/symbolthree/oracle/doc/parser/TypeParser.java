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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.thoughtworks.xstream.XStream;

import symbolthree.oracle.doc.DBConnection;
import symbolthree.oracle.doc.object.Argument;
import symbolthree.oracle.doc.object.Column;
import symbolthree.oracle.doc.object.DBObject;
import symbolthree.oracle.doc.object.Method;
import symbolthree.oracle.doc.object.Type;

public class TypeParser extends ObjectParser {
  Type typeObj;

  @Override
  public DBObject getODBjectInfo(DBObject obj, StringBuffer sb) {
    typeObj = (Type) obj;

    Connection conn;
    String     srcSQL = null;

    try {
      conn = DBConnection.getInstance().getConnection();

      String sql = "select B.OBJECT_ID     " +
                   "      , A.TYPECODE     " +
                   "      , A.ATTRIBUTES   " +
                   "      , A.METHODS      " +
                   "      , A.PREDEFINED   " +
                   "      , A.FINAL        " +
                   "      , A.INSTANTIABLE " +
                   "   FROM ALL_TYPES A   " +
                   "      , ALL_OBJECTS B " +
                   "  WHERE A.TYPE_NAME   = B.OBJECT_NAME" +
                   "    AND B.OBJECT_NAME = ?" +
                   "    AND B.OWNER = ?" +
                   "    AND B.object_type = 'TYPE'";

      PreparedStatement ps = conn.prepareStatement(sql);

      ps.setString(1, obj.getName());
      ps.setString(2, obj.getSchema());
      
      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        typeObj.setObjectID(rs.getInt(1));
        typeObj.setTypeCode(rs.getString(2));
        typeObj.setNoOfAttribute(rs.getInt(3));
        typeObj.setNoOfMethod(rs.getInt(4));
        typeObj.setPredefined(rs.getString(5));
        typeObj.setIsFinal(rs.getString(6));
        typeObj.setInstantiable(rs.getString(7));
      }

      rs.close();
      ps.close();

      // collection type
      if (typeObj.getTypeCode().equals("COLLECTION")) {

        ps = conn.prepareStatement("SELECT ELEM_TYPE_OWNER" +
        		                   "     , ELEM_TYPE_NAME" +
        		                   "  FROM ALL_COLL_TYPES" +
        		                   " WHERE TYPE_NAME=?" +
        		                   "   AND OWNER=?");
        ps.setString(1, typeObj.getName());
        ps.setString(2, typeObj.getSchema());
        rs = ps.executeQuery();
        while (rs.next()) {
          typeObj.setElemTypeOwner(rs.getString(1));
          typeObj.setElemTypeName(rs.getString(2));
        }
        rs.close();
        ps.close();
      }

      // type table
      if (typeObj.getTypeCode().equals("OBJECT")) {

        if (typeObj.getNoOfAttribute() > 0) {

          logger.debug("No of Attribute=" + typeObj.getNoOfAttribute());

          ps = conn.prepareStatement("SELECT ATTR_NAME" +
          		                       "     , ATTR_TYPE_NAME" +
          		                       "     , LENGTH" +
          		                       "     , ATTR_NO" +
          		                       "  FROM ALL_TYPE_ATTRS" +
          		                       " WHERE TYPE_NAME=?" +
          		                       "   AND OWNER=?" +
          		                       " ORDER BY ATTR_NO");
          
          ps.setString(1, typeObj.getName());
          ps.setString(2, typeObj.getSchema());
          
          rs = ps.executeQuery();
          while (rs.next()) {
            Column col = new Column();

            col.setName(rs.getString(1));
            col.setType(rs.getString(2));
            col.setLength(rs.getInt(3));
            col.setColumnID(rs.getInt(4));
            typeObj.addAttribute(col);
          }
        }

        if (typeObj.getNoOfMethod() > 0) {

       	  logger.debug("No of Method=" + typeObj.getNoOfMethod());

          ps = conn.prepareStatement("SELECT METHOD_NAME FROM ALL_TYPE_METHODS" +
          		                       " WHERE TYPE_NAME=? AND OWNER=? ORDER BY METHOD_NO");
          ps.setString(1, typeObj.getName());
          ps.setString(2, typeObj.getSchema());

          rs = ps.executeQuery();
          while (rs.next()) {
            String methodName = rs.getString(1);

            Method method = new Method();
            method.setName(methodName);

            PreparedStatement ps2 = conn.prepareStatement(
                                        "SELECT PARAM_NAME" +
                                        "      , PARAM_NO" +
                                        "      , PARAM_MODE" +
                                        "      , PARAM_TYPE_NAME" +
                                        "   FROM ALL_METHOD_PARAMS" +
                                        "  WHERE TYPE_NAME = ?" +
                                        "  WHERE OWNER = ?" +
                                        "    AND METHOD_NAME= ?" +
                                        "    AND PARAM_NAME <> 'SELF'" +
                                        "  ORDER BY PARAM_NO");

            ps2.setString(1, typeObj.getName());
            ps2.setString(2, typeObj.getSchema());
            ps2.setString(3, methodName);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
              Argument arg = new Argument();
              arg.setName(rs2.getString(1));
              arg.setPosition(rs2.getInt(2));
              arg.setInout(rs2.getString(3));
              arg.setType(rs2.getString(4));
              method.addArg(arg);
            }

            rs2.close();
            ps2.close();

            typeObj.addMethod(method);
          }
          rs.close();
          ps.close();

        }
      } // OBJECTS

    } catch (Exception e) {
      logger.catching(e);
      logger.error(typeObj.getName());
      logger.error(srcSQL);
    }

    return typeObj;
  }

  @Override
  public void serializer() {
    XStream xstream = new XStream();

    try {
      xstream.alias("DBObject", Type.class);
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

      xstream.alias("method", Method.class);
      xstream.useAttributeFor(Method.class, "name");

      xstream.alias("argument", Argument.class);
      xstream.useAttributeFor(Argument.class, "name");
      xstream.useAttributeFor(Argument.class, "type");
      xstream.useAttributeFor(Argument.class, "inout");
      xstream.useAttributeFor(Argument.class, "position");
      xstream.useAttributeFor(Argument.class, "origType");
      xstream.useAttributeFor(Argument.class, "defaultValue");

      String xmlString = xstream.toXML(typeObj);

      super.writeXMLOutput(typeObj, xmlString);
    } catch (Exception e) {
    	logger.catching(e);
    }
  }
}
