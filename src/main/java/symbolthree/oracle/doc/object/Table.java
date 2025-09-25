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

public class Table extends DBObject {
  private ArrayList<Column>     columns     = new ArrayList<>();
  private ArrayList<Index>      indices     = new ArrayList<>();
  private ArrayList<Constraint> constraints = new ArrayList<>();
  private ArrayList<Trigger>    triggers    = new ArrayList<>();

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
