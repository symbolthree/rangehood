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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/parser/ObjectParserFactory.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:36p $
 * $Revision: 3 $
 *****************************************************************************/



package symplik.oracle.doc.parser;

//~--- non-JDK imports --------------------------------------------------------

import symplik.oracle.doc.Logger;
import symplik.oracle.doc.object.DBObject;

public class ObjectParserFactory {
  public static ObjectParser getParser(DBObject dbObj) {
    String objectType = dbObj.getObjectType();
    String className  = "symplik.oracle.doc.parser." + objectType + "Parser";

    try {
      Class        clazz  = Class.forName(className);
      ObjectParser parser = (ObjectParser) clazz.newInstance();

      return parser;
    } catch (Exception e) {
      Logger.logError(e);

      return null;
    }
  }
}
