package symplik.oracle.doc.object;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collections;

public class DBObjectType {
  private String            typeName;
  private ArrayList<String> dbObjectNames = new ArrayList<String>();

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getTypeName() {
    return typeName;
  }

  public void addDbObjectName(String name) {

    // check any duplicate
    if (!dbObjectNames.contains(name)) {
      dbObjectNames.add(name);
    }
  }

  public void setDbObjectNames(ArrayList<String> dbObjectNames) {
    this.dbObjectNames = dbObjectNames;
  }

  public ArrayList<String> getDbObjectNames() {
    return dbObjectNames;
  }

  public void sortName() {
    Collections.sort(dbObjectNames);
  }
}
