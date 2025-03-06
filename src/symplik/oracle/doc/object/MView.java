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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/MView.java $
 * $Author: Christopher Ho $
 * $Date: 7/09/10 1:02p $
 * $Revision: 1 $
 *****************************************************************************/



package symplik.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class MView extends DBObject {
  private ArrayList<Column> columns = new ArrayList<Column>();
  private ArrayList<Index>  indices = new ArrayList<Index>();
  private String            updatable;
  private String            rewriteEnabled;
  private String            rewriteCapability;
  private String            refreshMode;
  private String            refreshMethod;
  private String            buildMode;
  private String            fastRefreshable;
  private String            source;

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

  public void setSource(String source) {
    this.source = source;
  }

  public String getSource() {
    return source;
  }

  public void setRewriteCapability(String rewriteCapability) {
    this.rewriteCapability = rewriteCapability;
  }

  public String getRewriteCapability() {
    return rewriteCapability;
  }

  public void setRefreshMode(String refreshMode) {
    this.refreshMode = refreshMode;
  }

  public String getRefreshMode() {
    return refreshMode;
  }

  public void setRefreshMethod(String refreshMethod) {
    this.refreshMethod = refreshMethod;
  }

  public String getRefreshMethod() {
    return refreshMethod;
  }

  public void setBuildMode(String buildMode) {
    this.buildMode = buildMode;
  }

  public String getBuildMode() {
    return buildMode;
  }

  public void setFastRefreshable(String fastRefreshable) {
    this.fastRefreshable = fastRefreshable;
  }

  public String getFastRefreshable() {
    return fastRefreshable;
  }

  public void setUpdatable(String updatable) {
    this.updatable = updatable;
  }

  public String getUpdatable() {
    return updatable;
  }

  public void setRewriteEnabled(String rewriteEnabled) {
    this.rewriteEnabled = rewriteEnabled;
  }

  public String getRewriteEnabled() {
    return rewriteEnabled;
  }
}
