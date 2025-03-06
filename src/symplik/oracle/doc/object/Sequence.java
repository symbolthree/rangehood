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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/Sequence.java $
 * $Author: Christopher Ho $
 * $Date: 7/09/10 1:02p $
 * $Revision: 2 $
 *****************************************************************************/



package symplik.oracle.doc.object;

public class Sequence extends DBObject {
  private double minValue;
  private double maxValue;
  private int    incrementBy;
  private String cycleFlag;
  private String orderFlag;
  private int    cacheSize;
  private double lastNumber;

  @Override
  public String getObjectType() {
    return "Sequence";
  }

  public void setMinValue(double minValue) {
    this.minValue = minValue;
  }

  public double getMinValue() {
    return minValue;
  }

  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
  }

  public double getMaxValue() {
    return maxValue;
  }

  public void setIncrementBy(int incrementBy) {
    this.incrementBy = incrementBy;
  }

  public int getIncrementBy() {
    return incrementBy;
  }

  public void setCycleFlag(String cycleFlag) {
    this.cycleFlag = cycleFlag;
  }

  public String getCycleFlag() {
    return cycleFlag;
  }

  public void setOrderFlag(String orderFlag) {
    this.orderFlag = orderFlag;
  }

  public String getOrderFlag() {
    return orderFlag;
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public int getCacheSize() {
    return cacheSize;
  }

  public void setLastNumber(double lastNumber) {
    this.lastNumber = lastNumber;
  }

  public double getLastNumber() {
    return lastNumber;
  }
}
