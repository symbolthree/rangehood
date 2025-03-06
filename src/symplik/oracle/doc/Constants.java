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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/Constants.java $
 * $Author: Christopher Ho $
 * $Date: 26/09/10 2:13p $
 * $Revision: 3 $
 *****************************************************************************/



package symplik.oracle.doc;

public interface Constants {
  public static int    LOG_DEBUG             = 0;
  public static int    LOG_ERROR             = 3;
  public static int    LOG_WARN              = 2;
  public static int    LOG_INFO              = 1;
  public static String LOG_OUTPUT_FILE       = "FILE";
  public static String LOG_OUTPUT_FILENAME   = "RANGEHOOD.log";
  public static String LOG_OUTPUT_SYSTEM_OUT = "SYSTEM.OUT";
  public static String RANGEHOOD_LOG_OUTPUT  = "RANGEHOOD_LOG_OUTPUT";
  public static String RANGEHOOD_LOG_LEVEL   = "RANGEHOOD_LOG_LEVEL";
  public static String RANGEHOOD_OUTPUT_DIR  = "RANGEHOOD_OUTPUT_DIR";
  public static String RESOURCE_BUNDLE       = "logging.properties";
}
