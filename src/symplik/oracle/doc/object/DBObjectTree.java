package symplik.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------

import java.util.*;

import symplik.oracle.doc.Logger;
import symplik.oracle.doc.Constants;

public class DBObjectTree implements Constants {
  private ArrayList<DBObjectType> dbObjectTypes = new ArrayList<DBObjectType>();

  public void addDBObject(String _objType, String _objName) {
    Logger.log(LOG_DEBUG, "Add DBObject " + _objType + "." + _objName);
    getDbObjectType(_objType).addDbObjectName(_objName);
  }
  

  public DBObjectType getDbObjectType(String name) {
    
    Logger.log(LOG_DEBUG, "getDbObjectType - " + name);
    
    DBObjectType type = null;
    
    Iterator<DBObjectType> itr = dbObjectTypes.iterator();

    while (itr.hasNext()) {
      type = (DBObjectType) itr.next();

      if (type.getTypeName().equals(name)) {
        return type;
      }
    }

    Logger.log(LOG_DEBUG, "create new DBObjectType of " + name);      
    type = new DBObjectType();
    type.setTypeName(name);
    dbObjectTypes.add(type);
    return type;      
  }

  public void setDbObjectTypes(ArrayList<DBObjectType> dbObjectTypes) {
    this.dbObjectTypes = dbObjectTypes;
  }

  public ArrayList<DBObjectType> getDbObjectTypes() {
    return dbObjectTypes;
  }
}
