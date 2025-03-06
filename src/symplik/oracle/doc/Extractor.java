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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/Extractor.java $
 * $Author: Christopher Ho $
 * $Date: 26/09/10 2:13p $
 * $Revision: 4 $
 *****************************************************************************/

package symplik.oracle.doc;

//~--- non-JDK imports --------------------------------------------------------

import SQLinForm_200.SQLForm;

import symplik.oracle.doc.object.DBObject;

//~--- JDK imports ------------------------------------------------------------

import java.io.Reader;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Extractor implements Constants {
  private PreparedStatement ps;
  private ResultSet         rs;

  public StringBuffer getContent(DBObject dbObj) {
    StringBuffer sb;

    if (dbObj.getObjectType().toUpperCase().equals("VIEW")) {
      sb = getViewContent(dbObj);
    } else if (dbObj.getObjectType().toUpperCase().equals("MVIEW")) {
      sb = getMViewContent(dbObj);
    } else {
      sb = getContentByMetadata(dbObj);
    }

    return sb;
  }

  private StringBuffer getMViewContent(DBObject dbObj) {
    StringBuffer sb = new StringBuffer();

    try {
      Connection        conn = DBConnection.getInstance().getConnection();
      String            sql  = "SELECT QUERY FROM USER_MVIEWS WHERE MVIEW_NAME=?";
      PreparedStatement ps   = conn.prepareStatement(sql);

      ps.setString(1, dbObj.getName());
      rs = ps.executeQuery();

      while (rs.next()) {
        sb.append(rs.getString(1));
      }

      rs.close();
      ps.close();
    } catch (Exception e) {
      Logger.logError(e);
    }

    return formatSQL(sb);
  }

  private StringBuffer getViewContent(DBObject dbObj) {
    StringBuffer sb = new StringBuffer();

    try {
      Connection conn = DBConnection.getInstance().getConnection();
      String     sql  = "SELECT TEXT FROM USER_VIEWS WHERE VIEW_NAME=?";

      ps = conn.prepareStatement(sql);
      ps.setString(1, dbObj.getName());
      rs = ps.executeQuery();

      while (rs.next()) {
        //Logger.log(LOG_DEBUG, "in getViewContent");
        sb.append(rs.getString(1));
      }

      rs.close();
      ps.close();
    } catch (Exception e) {
      Logger.logError(e);
    }

    return formatSQL(sb);
  }

  private StringBuffer getContentByMetadata(DBObject dbObj) {
    StringBuffer sb = new StringBuffer();

    try {
      Connection conn = DBConnection.getInstance().getConnection();
      String     sql  = "SELECT DBMS_METADATA.GET_DDL(?,?,?) FROM DUAL";

      ps = conn.prepareStatement(sql);

      String objectType = dbObj.getObjectType().toUpperCase();

      if (objectType.equals("PACKAGE")) {
        objectType = "PACKAGE_SPEC";
      } else if (objectType.equals("TYPE")) {
        objectType = "TYPE_SPEC";
      }

      ps.setString(1, objectType);
      ps.setString(2, dbObj.getName());
      ps.setString(3, dbObj.getSchema());
      rs = ps.executeQuery();

      while (rs.next()) {
        Clob   clob   = rs.getClob(1);
        Reader is     = clob.getCharacterStream();
        int    count  = 0;
        long   len    = clob.length();
        char[] buffer = new char[1024];

        while ((count = is.read(buffer)) != -1) {
          sb.append(buffer);
        }

        sb.setLength((int) len);
      }

      rs.close();
      ps.close();

      SQLForm sqlform   = new SQLForm();
      String  formatted = sqlform.formatSQLAsString(sb.toString());

      sb.setLength(0);
      sb.append(formatted);
    } catch (Exception e) {
      Logger.logError(e);
    }

    return formatSQL(sb);
  }

  private StringBuffer formatSQL(StringBuffer sb) {
    SQLForm sqlform = new SQLForm();

    sqlform.setDisplayOfWarningMsg(true);

    String formatted = sqlform.formatSQLAsString(sb.toString());

    /*
     * for some very complex query this formatter does not work and just give empty string
     * so we can only do the simple formatting...
     */
    formatted = formatted.trim();

    if ((formatted == null) || formatted.equals("")) {
      Logger.log(LOG_WARN, "SQLFormatter fails. Use simple formatting");
      formatted = sb.toString();
      formatted = replaceAll(formatted, ",", "\n" + ",");
      formatted = replaceAll(formatted, " AND", "\n" + "AND ");
      formatted = replaceAll(formatted, "||", "\n" + "||");
    }

    sb.setLength(0);
    sb.append(formatted);

    return sb;
  }

  public static String replaceAll(String source, String toReplace, String replacement) {
    int idx = source.lastIndexOf(toReplace);

    if (idx != -1) {
      StringBuffer ret = new StringBuffer(source);

      ret.replace(idx, idx + toReplace.length(), replacement);

      while ((idx = source.lastIndexOf(toReplace, idx - 1)) != -1) {
        ret.replace(idx, idx + toReplace.length(), replacement);
      }

      source = ret.toString();
    }

    return source;
  }
}
