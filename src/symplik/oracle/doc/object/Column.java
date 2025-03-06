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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/Column.java $
 * $Author: Christopher Ho $
 * $Date: 7/09/10 1:02p $
 * $Revision: 2 $
 *****************************************************************************/



package symplik.oracle.doc.object;

public class Column {
  private String name;
  private String type;
  private String nullable;
  private int    columnID;
  private int    length;
  private String defaultValue;
  private String comment;
  private String sourceColumn;

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

  public void setNullable(String nullable) {
    this.nullable = nullable;
  }

  public String getNullable() {
    return nullable;
  }

  public void setColumnID(int columnID) {
    this.columnID = columnID;
  }

  public int getColumnID() {
    return columnID;
  }

  public void setDefaultValue(String defaultValue) {
    if (defaultValue == null) {
      this.defaultValue = "";
    } else {
      this.defaultValue = defaultValue;
    }
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setComment(String comment) {
    if (comment == null) {
      this.comment = "";
    } else {
      this.comment = comment;
    }
  }

  public String getComment() {
    return comment;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public int getLength() {
    return length;
  }

  public void setSourceColumn(String sourceColumn) {
    this.sourceColumn = sourceColumn;
  }

  public String getSourceColumn() {
    return sourceColumn;
  }
}
