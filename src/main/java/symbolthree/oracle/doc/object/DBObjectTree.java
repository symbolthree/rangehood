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

package symbolthree.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import symbolthree.oracle.doc.Constants;

public class DBObjectTree implements Constants {
	
  private ArrayList<DBObjectType> dbObjectTypes = new ArrayList<>();

  static final Logger logger = LogManager.getLogger(DBObjectTree.class.getName());  
  
  public void addDBObject(String _objType, String _objName) {
	    logger.debug("Add DBObject " + _objType + "." + _objName);
	    getDbObjectType(_objType).addDbObjectName(_objName);
}
  
  public void addDBObject(String _objType, String _objName, String _objSchema) {
	    logger.debug("Add DBObject " + _objType + "." + _objSchema + "." + _objName);
	    getDbObjectType(_objType).addDbObjectName(_objName, _objSchema);
  }

  public DBObjectType getDbObjectType(String name) {

	  logger.debug("getDbObjectType - " + name);

    DBObjectType type = null;

    Iterator<DBObjectType> itr = dbObjectTypes.iterator();

    while (itr.hasNext()) {
      type = itr.next();

      if (type.getTypeName().equals(name)) {
        return type;
      }
    }

    logger.debug("create new DBObjectType of " + name);
    type = new DBObjectType();
    type.setTypeName(name);
    dbObjectTypes.add(type);
    return type;
  }

  public void setDbObjectTypes(ArrayList<DBObjectType> dbObjectTypes) {
    this.dbObjectTypes = dbObjectTypes;
  }

  public ArrayList<DBObjectType> getDbObjectTypes() {
    return dbObjectTypes;
  }
}
