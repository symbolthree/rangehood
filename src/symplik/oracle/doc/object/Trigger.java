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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/object/Trigger.java $
 * $Author: Christopher Ho $
 * $Date: 7/09/10 1:02p $
 * $Revision: 2 $
 *****************************************************************************/



package symplik.oracle.doc.object;

public class Trigger extends DBObject {
  private String event;
  private String type;
  private String tableOwner;
  private String baseObjectType;
  private String referencingNames;
  private String tableName;
  private String whenClause;
  private String body;

  @Override
  public String getObjectType() {
    return "Trigger";
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String getEvent() {
    return event;
  }

  public void setTableOwner(String tableOwner) {
    this.tableOwner = tableOwner;
  }

  public String getTableOwner() {
    return tableOwner;
  }

  public void setBaseObjectType(String baseObjectType) {
    this.baseObjectType = baseObjectType;
  }

  public String getBaseObjectType() {
    return baseObjectType;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setWhenClause(String whenClause) {
    if (whenClause != null) {
      this.whenClause = whenClause.trim();
    }
  }

  public String getWhenClause() {
    return whenClause;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    return body;
  }

  public void setReferencingNames(String referencingNames) {
    this.referencingNames = referencingNames;
  }

  public String getReferencingNames() {
    return referencingNames;
  }
}
