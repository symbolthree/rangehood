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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/DBType.java $
 * $Author: Administrator $
 * $Date: 12/10/10 1:40p $
 * $Revision: 1 $
 *****************************************************************************/

package symplik.oracle.doc.object;

import java.util.ArrayList;

public class DBType extends DBObject {
  private String typeCode;
  private int noOfAttribute;
  private int noOfMethod;
  private String predefined;
  private String isFinal;
  private String instantiable;
  private ArrayList<Column> attributes = new ArrayList<Column>();
  private ArrayList<String> methods = new ArrayList<String>();
  
  
  public void setTypeCode(String typeCode) {
    this.typeCode = typeCode;
  }
  public String getTypeCode() {
    return typeCode;
  }
  public void setNoOfAttribute(int noOfAttribute) {
    this.noOfAttribute = noOfAttribute;
  }
  public int getNoOfAttribute() {
    return noOfAttribute;
  }
  public void setNoOfMethod(int noOfMethod) {
    this.noOfMethod = noOfMethod;
  }
  public int getNoOfMethod() {
    return noOfMethod;
  }
  public void setPredefined(String predefined) {
    this.predefined = predefined;
  }
  public String getPredefined() {
    return predefined;
  }
  public void setIsFinal(String isFinal) {
    this.isFinal = isFinal;
  }
  public String getIsFinal() {
    return isFinal;
  }
  public void setInstantiable(String instantiable) {
    this.instantiable = instantiable;
  }
  public String getInstantiable() {
    return instantiable;
  }
  public void addAttribute(Column attribute) {
    attributes.add(attribute);
  }
  public void setAttributes(ArrayList<Column> attributes) {
    this.attributes = attributes;
  }
  public ArrayList<Column> getAttributes() {
    return attributes;
  }
  public void addMethod(String method) {
    methods.add(method);
  }  
  public void setMethods(ArrayList<String> methods) {
    this.methods = methods;
  }
  public ArrayList<String> getMethods() {
    return methods;
  }
  
}
