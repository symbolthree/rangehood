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

package symbolthree.oracle.doc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

//~--- JDK imports ------------------------------------------------------------

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//~--- non-JDK imports --------------------------------------------------------

import oracle.dbtools.app.Format;
import oracle.dbtools.app.Format.Breaks;
import symbolthree.oracle.doc.object.DBObject;

public class Extractor implements Constants {
  private PreparedStatement ps;
  private ResultSet         rs;
  
  private Format            format = new Format();
  private HashMap<String, Object> formatOptions = new HashMap<String, Object>();
  
  static final Logger logger = LogManager.getLogger(Extractor.class.getName());  
  
  public Extractor() {
	  // default SQL format settings
	  formatOptions.put("identSpaces", Integer.valueOf(2));
	  formatOptions.put("breaksAfterSelect", Boolean.valueOf(false));
	  formatOptions.put("breaksComma", Breaks.Before);
	  formatOptions.put("alignAssignments", Boolean.valueOf(true));
	  format.setOptions(formatOptions);
  }
  
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
      logger.catching(e);
    }

    return formatSQL(sb);
  }

  private StringBuffer getViewContent(DBObject dbObj) {
    StringBuffer sb = new StringBuffer();

    try {
      Connection conn = DBConnection.getInstance().getConnection();
      String     sql  = "SELECT TEXT FROM ALL_VIEWS WHERE VIEW_NAME=? AND OWNER=?";

      ps = conn.prepareStatement(sql);
      ps.setString(1, dbObj.getName());
      ps.setString(2, dbObj.getSchema());
      rs = ps.executeQuery();

      while (rs.next()) {
        //Logger.log(LOG_DEBUG, "in getViewContent");
        sb.append(rs.getString(1));
      }

      rs.close();
      ps.close();
    } catch (Exception e) {
      logger.catching(e);
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

      if (objectType.equals("PACKAGE-SPEC")) {
        objectType = "PACKAGE";
      } else if (objectType.equals("PACKAGE-BODY")) {
        objectType = "PACKAGE BODY";
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
        sb.setLength((int)len);
      }

      rs.close();
      ps.close();

    } catch (Exception e) {
    	logger.catching(e);
    }
    return formatSQL(sb);
  }

  private StringBuffer formatSQL(StringBuffer sb) {
	 
    String formatted = null;
	
    try { 
      formatted = format.format(sb.toString());
	} catch (IOException ioe) {
	}
	 
    if ((formatted == null) || formatted.equals("")) {
        logger.info("SQLFormatter fails. Use simple formatting");
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

  public void saveSQLToFile(StringBuffer sb, String outputFile) {
	logger.debug("Writing " + outputFile);
	File file = new File(outputFile);
	File dir = new File(file.getParent());
	if (! dir.exists()) dir.mkdirs();

    try (
    FileOutputStream fos = new FileOutputStream(outputFile);
    OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
    BufferedWriter writer = new BufferedWriter(osw)) {
      writer.write(sb.toString());
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
}