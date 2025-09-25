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

public class Argument {
  private String name     = "";
  private String inout    = "";
  private String origType = "";
  private String type     = "";
  private int    position;
  private String defaultValue = "";
  private String defaulted = "";

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setInout(String inout) {
    this.inout = inout;
  }

  public String getInout() {
    return inout;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
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

  public void setPosition(int position) {
    this.position = position;
  }

  public int getPosition() {
    return position;
  }

  public void setOrigType(String origType) {
    this.origType = origType;
  }

  public String getOrigType() {
    return origType;
  }

public String getDefaulted() {
	return defaulted;
}

public void setDefaulted(String defaulted) {
	this.defaulted = defaulted;
}
}
