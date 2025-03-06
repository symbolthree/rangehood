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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/Type.java $
 * $Author: Christopher Ho $
 * $Date: 23/09/10 12:35p $
 * $Revision: 3 $
 *****************************************************************************/

package symplik.oracle.doc.object;

import java.util.ArrayList;

public class Type extends DBObject {
  private String typeCode;
  private int noOfAttribute;
  private int noOfMethod;
  private String predefined;
  private String isFinal;
  private String instantiable;
  private ArrayList<Column> attributes = new ArrayList<Column>();
  private ArrayList<Method> methods    = new ArrayList<Method>();
  private String elemTypeOwner;
  private String elemTypeName;
  
  
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
  public void addMethod(Method method) {
    methods.add(method);
  }  
  public void setMethods(ArrayList<Method> methods) {
    this.methods = methods;
  }
  public ArrayList<Method> getMethods() {
    return methods;
  }
  public void setElemTypeOwner(String elemTypeOwner) {
    this.elemTypeOwner = elemTypeOwner;
  }
  public String getElemTypeOwner() {
    return elemTypeOwner;
  }
  public void setElemTypeName(String elemTypeName) {
    this.elemTypeName = elemTypeName;
  }
  public String getElemTypeName() {
    return elemTypeName;
  }
  
}
