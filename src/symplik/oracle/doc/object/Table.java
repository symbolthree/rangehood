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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/Table.java $
 * $Author: Christopher Ho $
 * $Date: 7/09/10 1:02p $
 * $Revision: 2 $
 *****************************************************************************/



package symplik.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class Table extends DBObject {
  private ArrayList<Column>     columns     = new ArrayList<Column>();
  private ArrayList<Index>      indices     = new ArrayList<Index>();
  private ArrayList<Constraint> constraints = new ArrayList<Constraint>();
  private ArrayList<Trigger>    triggers    = new ArrayList<Trigger>();

  @Override
  public String getObjectType() {
    return "Table";
  }

  public void addColumn(Column col) {
    columns.add(col);
  }

  public void setColumns(ArrayList<Column> cols) {
    this.columns = cols;
  }

  public ArrayList<Column> getColumns() {
    return columns;
  }

  public void addIndex(Index ind) {
    indices.add(ind);
  }

  public void setIndices(ArrayList<Index> inds) {
    this.indices = inds;
  }

  public ArrayList<Index> getIndices() {
    return indices;
  }

  public void addConstraint(Constraint con) {
    constraints.add(con);
  }

  public void setConstraint(ArrayList<Constraint> cons) {
    this.constraints = cons;
  }

  public ArrayList<Constraint> getConstraint() {
    return constraints;
  }

  public void addTrigger(Trigger trigger) {
    triggers.add(trigger);
  }

  public void setTriggers(ArrayList<Trigger> triggers) {
    this.triggers = triggers;
  }

  public ArrayList<Trigger> getTriggers() {
    return triggers;
  }
}
