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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/Logger.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:34p $
 * $Revision: 1 $
 *****************************************************************************/


package symplik.oracle.doc;

//~--- JDK imports ------------------------------------------------------------

import java.io.*;

import java.text.SimpleDateFormat;

import java.util.Properties;

public class Logger implements Constants {

  private static Logger           logger;
  private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyMMdd.HHmmss");
  private static FileWriter       logWriter;
  private Properties              prop;
  
  public Logger() {
    prop = new Properties();

    try {
      File            file = new File(System.getProperty("user.dir"), RESOURCE_BUNDLE);
      FileInputStream is   = new FileInputStream(file);

      prop.load(is);
      is.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Logger instance() {
    if (logger == null) {
      logger = new Logger();
    }

    return logger;
  }
  
  public static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter  pw = new PrintWriter(sw, true);

    t.printStackTrace(pw);
    pw.flush();
    sw.flush();

    return sw.toString();
  }

  public static void logError(Throwable t) {
    if (System.getProperty(RANGEHOOD_LOG_LEVEL).equals(logLevelStr(LOG_DEBUG))) {
      log(LOG_ERROR, getStackTrace(t));
    } else {
      log(LOG_ERROR, t.getLocalizedMessage());
    }
  }

  public static void log(int logLevel, String logMsg) {
    int    logThreshold = logLevelInt(System.getProperty(RANGEHOOD_LOG_LEVEL));
    String logOutput    = System.getProperty(RANGEHOOD_LOG_OUTPUT);
    String logLine      = timeFormat.format(new java.util.Date()) + " [" + logLevelStr(logLevel) + "] - " + logMsg;

    if ((logLevel >= logThreshold) || (logLevel == LOG_ERROR)) {
      if (logOutput.equals(LOG_OUTPUT_SYSTEM_OUT)) {
        System.out.println(logLine);
      } else if (logOutput.equals(LOG_OUTPUT_FILE)) {
        try {
          logWriter.write(logLine + System.getProperty("line.separator"));
          logWriter.flush();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }
  }

  public static String logLevelStr(int logLevel) {
    if (logLevel == LOG_DEBUG) {
      return "DEBUG";
    }

    if (logLevel == LOG_INFO) {
      return "INFO";
    }

    if (logLevel == LOG_WARN) {
      return "WARN";
    }

    if (logLevel == LOG_ERROR) {
      return "ERROR";
    }

    return String.valueOf("UNKNOWN");
  }

  public static int logLevelInt(String logLevel) {
    if (logLevel.equals("DEBUG")) {
      return LOG_DEBUG;
    }

    if (logLevel.equals("INFO")) {
      return LOG_INFO;
    }

    if (logLevel.equals("WARN")) {
      return LOG_WARN;
    }

    if (logLevel.equals("ERROR")) {
      return LOG_ERROR;
    }

    return LOG_ERROR;
  }

  public static void initializeLogging() throws IOException {

    // default logging is: log level = INFO; log output = SYSTEM.OUT
    String val = null;

    val = Logger.instance().getStr(RANGEHOOD_LOG_LEVEL);

    if (Logger.isEmpty(val)) {
      System.setProperty(RANGEHOOD_LOG_LEVEL, "INFO");
    } else {
      System.setProperty(RANGEHOOD_LOG_LEVEL, val);
    }

    val = Logger.instance().getStr(RANGEHOOD_LOG_OUTPUT);

    if (Logger.isEmpty(val)) {
      System.setProperty(RANGEHOOD_LOG_OUTPUT, LOG_OUTPUT_SYSTEM_OUT);
    } else {
      System.setProperty(RANGEHOOD_LOG_OUTPUT, val);
    }

    if (val.equals(LOG_OUTPUT_FILE)) {
      File logFile = new File(System.getProperty("user.dir"), LOG_OUTPUT_FILENAME);

      if (logFile.exists()) {
        logFile.delete();
      }

      logWriter = new FileWriter(logFile);
    }
  }

  public String getStr(String key) {
    String _str = prop.getProperty(key);

    if (_str == null) {
      return null;
    } else {
      return _str;
    }
  }

  public static boolean isEmpty(String str) {
    if ((str == null) || (str.trim().length() == 0)) {
      return true;
    } else {
      return false;
    }
  }

  public String getOutputDir() throws Exception {
    String output = prop.getProperty(RANGEHOOD_OUTPUT_DIR);
    File   dir;

    dir = new File(output);

    if (dir.exists()) {
      return dir.getAbsolutePath();
    } else {
      dir = new File(System.getProperty("user.dir"), output);

      if (dir.exists()) {
        return dir.getAbsolutePath();
      } else {
        throw new Exception("Output directory not found:" + dir.getAbsolutePath());
      }
    }
  }
}
