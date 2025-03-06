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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/DBConnection.java $
 * $Author: Christopher Ho $
 * $Date: 30/09/10 2:00p $
 * $Revision: 5 $
 *****************************************************************************/


package symplik.oracle.doc;

//~--- non-JDK imports --------------------------------------------------------

import oracle.jdbc.*;

//~--- JDK imports ------------------------------------------------------------

import java.sql.*;

public class DBConnection implements Constants {
  private static DBConnection instance = null;
  private Connection          connection;

  protected DBConnection(String url, String username, String password) throws SQLException {
    DriverManager.registerDriver(new OracleDriver());
    Logger.log(LOG_DEBUG, "url=" + url);
    Logger.log(LOG_DEBUG, "username=" + username);
    connection = DriverManager.getConnection(url, username, password);
    ResultSet rs = connection.createStatement().executeQuery("select a.version || ' on ' || b.platform_name from v$instance a, v$database b");
    while (rs.next()) {
      Logger.log(LOG_INFO, "Oracle Database " + rs.getString(1));
    }
    setSessionLang("AMERICAN");
    setMetaDataSettings();
  }

  public static DBConnection getInstance(String url, String username, String password) throws SQLException {
    if (instance == null) {
      instance = new DBConnection(url, username, password);
    }

    return instance;
  }

  public static DBConnection getInstance() throws Exception {
    if (instance == null) {
      throw new Exception("DBConnection is not instantiated.");
    }

    return instance;
  }

  public Connection getConnection() {
    return connection;
  }

  public void setSessionLang(String language) throws SQLException {
    Logger.log(LOG_DEBUG, "Set NLS_LANGUAGE=AMERICAN");

    Statement stmt = connection.createStatement();

    stmt.execute("alter session set NLS_LANGUAGE='" + language + "'");
  }

  public void setMetaDataSettings() throws SQLException {
    String sql = "BEGIN "
                 + "dbms_metadata.set_transform_param(dbms_metadata.session_transform, 'SEGMENT_ATTRIBUTES', false);"
                 + "dbms_metadata.set_transform_param(dbms_metadata.session_transform, 'PRETTY', false);"
                 + "dbms_metadata.set_transform_param(dbms_metadata.session_transform, 'TABLESPACE', false);"
                 + "dbms_metadata.set_transform_param(dbms_metadata.session_transform, 'STORAGE', false);END;";
    Statement stmt = connection.createStatement();

    stmt.execute(sql);
  }

}
