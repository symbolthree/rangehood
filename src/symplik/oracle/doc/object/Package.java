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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/Package.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:35p $
 * $Revision: 3 $
 *****************************************************************************/



package symplik.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class Package extends DBObject {
  private ArrayList<SubProgram> subPrograms = new ArrayList<SubProgram>();
  private ArrayList<SubType>       types       = new ArrayList<SubType>();

  public void setSubPrograms(ArrayList<SubProgram> subPrograms) {
    this.subPrograms = subPrograms;
  }

  public ArrayList<SubProgram> getSubPrograms() {
    return subPrograms;
  }

  public void addSubProgram(SubProgram subProg) {
    subPrograms.add(subProg);
  }

  public void setTypes(ArrayList<SubType> types) {
    this.types = types;
  }

  public ArrayList<SubType> getTypes() {
    return types;
  }

  public void addType(SubType type) {
    types.add(type);
  }
}
