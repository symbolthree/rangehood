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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/SubProgram.java $
 * $Author: Christopher Ho $
 * $Date: 7/09/10 1:02p $
 * $Revision: 2 $
 *****************************************************************************/



package symplik.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class SubProgram extends DBObject {
  private int                 subprogramID;
  private int                 overload;
  private ArrayList<Argument> args = new ArrayList<Argument>();

  public void addArg(Argument arg) {
    args.add(arg);
  }

  public ArrayList<Argument> getArgs() {
    return args;
  }

  public void setSubprogramID(int subprogramID) {
    this.subprogramID = subprogramID;
  }

  public int getSubprogramID() {
    return subprogramID;
  }

  public void setOverload(int overload) {
    if (overload == 0) {
      this.overload = -1;
    } else {
      this.overload = overload;
    }
  }

  public int getOverload() {
    return overload;
  }
}
