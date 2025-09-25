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
import java.util.Collections;

public class DBObjectType {
  private String            typeName;
  private ArrayList<String> dbObjectNames = new ArrayList<>();

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }

  public void addDbObjectName(String name) {
    // check any duplicate
    if (!dbObjectNames.contains(name)) {
      dbObjectNames.add(name);
    }
  }
  
  public void addDbObjectName(String name, String schema) {
	String fullName = schema + "." + name;
    // check any duplicate
    if (!dbObjectNames.contains(fullName)) {
      dbObjectNames.add(fullName);
    }
  }

  public void setDbObjectNames(ArrayList<String> dbObjectNames) {
    this.dbObjectNames = dbObjectNames;
  }

  public ArrayList<String> getDbObjectNames() {
    return dbObjectNames;
  }

  public void sortName() {
    Collections.sort(dbObjectNames);
  }
}
