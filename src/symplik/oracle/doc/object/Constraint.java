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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/Constraint.java $
 * $Author: Christopher Ho $
 * $Date: 7/09/10 1:02p $
 * $Revision: 2 $
 *****************************************************************************/



package symplik.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class Constraint {
  private String            name;
  private String            type;
  private ArrayList<String> columns = new ArrayList<String>();
  private String            details;
  private String            rname;
  private ArrayList<String> rcolumns = new ArrayList<String>();

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void addColumn(String column) {
    this.columns.add(column);
  }

  public ArrayList<String> getColumns() {
    return columns;
  }

  public void setDetails(String details) {
    if (details == null) {
      this.details = "";
    } else {
      this.details = details;
    }
  }

  public String getDetails() {
    return details;
  }

  public void setRname(String rname) {
    this.rname = rname;
  }

  public String getRname() {
    return rname;
  }

  public void addRcolumn(String rcol) {
    this.rcolumns.add(rcol);
  }

  public ArrayList<String> getRcolumns() {
    return rcolumns;
  }
}
