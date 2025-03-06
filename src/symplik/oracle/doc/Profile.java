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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/Profile.java $
 * $Author: Christopher Ho $
 * $Date: 28/09/10 12:59p $
 * $Revision: 8 $
 *****************************************************************************/


package symplik.oracle.doc;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDomDriver;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.ProcessingInstruction;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.object.DBObjectTree;
import symplik.oracle.doc.object.DBObjectType;

//~--- JDK imports ------------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Connection;
import java.sql.ResultSet;

import java.util.*;

public class Profile implements Constants {
  private static Profile  myProfile = null;
  private static Document document  = null;

  private String          outputDir = null;
  private File            profileFile;
  private static String   PROFILE_FILENAME = "RANGEHOOD.xml";
  private XPath           xpath;

  private ArrayList<DBObject> allObjects = new ArrayList<DBObject>();

  protected Profile(String profileName) throws Exception {

      SAXBuilder builder = new SAXBuilder(true);

      profileFile = new File(System.getProperty("user.dir") + File.separator + PROFILE_FILENAME);
      Logger.log(LOG_DEBUG, "Loading " + profileFile.getAbsolutePath() + "...");
      document = builder.build(profileFile);
      Logger.log(LOG_DEBUG, profileFile.getAbsolutePath() + " loaded.");
      // check valid profileName
      xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']");
      List list = xpath.selectNodes(document);

      if (list.size() == 0) {
        Logger.log(LOG_ERROR, "Profile " + profileName + " not found.");
        throw new JDOMException();
      } else {
        setOutputDir(profileName);
      }
  }

  public static Profile getInstance() {
    return myProfile;
  }
  
  public static Profile getInstance(String profileName) throws Exception {
    if (myProfile == null) {
      myProfile = new Profile(profileName);
    }

    return myProfile;
  }

  public String getOutputDir() {
    return outputDir;
  }
  
  private void setOutputDir(String profileName) throws Exception {
    
    String dir = null;
    String attrVal = null;
    
    if (outputDir==null) {
      xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']/@output");
      Attribute attr = (Attribute) xpath.selectSingleNode(document);
      attrVal  = attr.getValue();
      
      File file = new File(System.getProperty("user.dir"), attrVal);
      if (file.exists() && file.isDirectory()) {
        dir = file.getAbsolutePath();
      }

      if (! file.exists()) {
        file = new File(attrVal);
        if (file.exists() && file.isDirectory()) {
          dir = file.getAbsolutePath();
        }
      }

    } else {
      dir = outputDir;
    }
    
    if (dir==null) {
      throw new Exception ("Unable to find directory : " + attrVal);
    } else {
      Logger.log(LOG_DEBUG, "Output dir=" + dir);
      outputDir = dir;
    }
  }
  
  private boolean isAppendMode(String profileName) throws Exception {
    xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']/@append");

    Attribute attr = (Attribute) xpath.selectSingleNode(document);
    String    str  = attr.getValue().toUpperCase();

    if (str.equals("Y")) {
      return true;
    } else {
      return false;
    }
  }

  public String getDatabaseURL(String profileName) throws Exception {
    xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']/DATABASE/URL");

    Element ele = (Element) xpath.selectSingleNode(document);

    return ele.getValue();
  }

  public String getDatabaseUsername(String profileName) throws Exception {
    xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']/DATABASE/USERNAME");

    Element ele = (Element) xpath.selectSingleNode(document);

    return ele.getValue();
  }

  public String getDatabasePassword(String profileName) throws Exception {
    xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']/DATABASE/PASSWORD");

    Element ele = (Element) xpath.selectSingleNode(document);

    return ele.getValue();
  }

  public ArrayList<DBObject> getObjects(String profileName) throws Exception {
    DBObjectTree objTreeMap = new DBObjectTree();
    String       schema     = getDatabaseUsername(profileName);
    Connection   conn       = DBConnection.getInstance().getConnection();

    // get all distinct object type
    xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName
                              + "']/DBOBJECT");

    List<Element> list = xpath.selectNodes(document);

    String sql = "SELECT DECODE(VIEW1.OBJECT_TYPE, 'MATERIALIZED VIEW', 'MVIEW', VIEW1.OBJECT_TYPE)" +
    		         "     , VIEW1.OBJECT_NAME FROM " +
                 "(SELECT OBJECT_TYPE, OBJECT_NAME FROM USER_OBJECTS WHERE 1=0";    
    
    for (Iterator<Element> itr = list.iterator(); itr.hasNext(); ) {
      Element ele    = itr.next();
      String objType = ele.getAttributeValue("type").toUpperCase();
      String objName = ele.getAttributeValue("name") ;
      String include = ele.getAttributeValue("include");
      
      if (objType.equals("MVIEW")) objType="MATERIALIZED VIEW";
      if (objType.equals("ALL"))   objType="%";

      if (include.equals("Y")) {
        sql = sql + " UNION ";
      } else if (include.equals("N")) {
        sql = sql + " MINUS ";
      }
      
      sql = sql + "SELECT A.OBJECT_TYPE" +
 		              "     , A.OBJECT_NAME" +
 		              "  FROM USER_OBJECTS A" + 
                  " WHERE A.OBJECT_TYPE LIKE '" + objType + "'" +
                  "   AND A.OBJECT_NAME LIKE '" + objName + "'";
      
      // if search for Table, eliminate Materialized View (which is shown as TABLE in xxx_objects)
      if (objType.equals("TABLE")) {
        sql = sql + " AND NOT EXISTS (SELECT 'X' FROM USER_OBJECTS B WHERE"
              + " B.OBJECT_TYPE='MATERIALIZED VIEW' AND A.OBJECT_NAME=B.OBJECT_NAME)";
      }
    }
    
    sql = sql + ") VIEW1 WHERE VIEW1.OBJECT_TYPE IN (" +
    		        "'TABLE','FUNCTION','PROCEDURE','PACKAGE','VIEW','SEQUENCE','TRIGGER','MATERIALIZED VIEW','TYPE'" +
    		        ") ORDER BY OBJECT_TYPE, OBJECT_NAME";
    
    Logger.log(LOG_DEBUG, "SQL : " + sql);

    ResultSet rs = conn.createStatement().executeQuery(sql);
    
    while (rs.next()) {
      
      String objType = rs.getString(1);
      String objName = rs.getString(2); 
      
      objTreeMap.addDBObject(objType, objName);
    }

    // generate all dbObjects
    ArrayList<DBObjectType> set = objTreeMap.getDbObjectTypes();
    Iterator<DBObjectType>  itr = set.iterator();

    while (itr.hasNext()) {
      DBObjectType type     = itr.next();
      String       typeName = type.getTypeName();

      // get class name of each type (1st letter capital, the rest lowercase, except MView, Type)
      if (typeName.equals("MVIEW")) {
        typeName = "MView";
      } else if (typeName.equals("DBTYPE")) {
        typeName = "DBType";
      } else {  
        typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1, typeName.length()).toLowerCase();
      }

      ArrayList<String> objs = type.getDbObjectNames();
      Iterator<String>  itr2 = objs.iterator();

      while (itr2.hasNext()) {
        Class    clazz = Class.forName("symplik.oracle.doc.object." + typeName);
        DBObject obj   = (DBObject) clazz.newInstance();

        obj.setName(itr2.next());
        obj.setSchema(schema);
        obj.setObjectType(typeName);
        allObjects.add(obj);
        Logger.log(LOG_INFO, "Create dbObject " + obj.getObjectType() + ": " + obj.getName());
      }
    }

    boolean appendMode = this.isAppendMode(profileName);

    if (!appendMode) {
      xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']/@template");

      Attribute attr         = (Attribute) xpath.selectSingleNode(document);
      String    templateName = attr.getValue();
      File      srcFolder    = new File(System.getProperty("user.dir") + File.separator + "template" + File.separator
                                        + templateName);

      if (!srcFolder.exists()) {
        throw new Exception("Template directory not found:" + srcFolder.getAbsolutePath());
      }

      copyFolder(srcFolder, new File(getOutputDir()));
    }

    
    Logger.log(LOG_DEBUG, "DBObject Type Size: " + objTreeMap.getDbObjectTypes().size());
    
    createAllObjectsXMLFile(objTreeMap, appendMode);
    //createObjectTypeXMLFiles(objTreeMap, appendMode);

    return allObjects;
  }

  private void createAllObjectsXMLFile(DBObjectTree objTreeMap, boolean appendMode) {
    if (appendMode) {
      Logger.log(LOG_INFO, "Append=YES");
      appendAllObjectsXMLFile(objTreeMap);
    } else {
      Logger.log(LOG_INFO, "Append=NO");
      createAllObjectsXMLFile(objTreeMap);
      createObjectTypeXMLFiles(objTreeMap);
    }
  }

  // append to dbObjects XML file
  private void appendAllObjectsXMLFile(DBObjectTree objTreeMap) {
    XStream xstream = new XStream(new JDomDriver());

    xstream.alias("dbObjectTree", DBObjectTree.class);
    xstream.alias("dbObjectType", DBObjectType.class);
    xstream.useAttributeFor(DBObjectType.class, "typeName");
    xstream.alias("name", String.class);

    try {
      File file = getXMLOutput("DBOBJECTS");

      Logger.log(LOG_DEBUG, "Check DBOBJECTS file: " + file.getAbsoluteFile() + " - " + file.exists());

      if (file.exists()) {
        InputStream  is       = new FileInputStream(file);
        DBObjectTree lastTree = (DBObjectTree) xstream.fromXML(is);

        Logger.log(LOG_DEBUG, "done convert xml to object");

        Iterator<DBObjectType> currentTreeItr = objTreeMap.getDbObjectTypes().iterator();

        while (currentTreeItr.hasNext()) {
          DBObjectType currentType     = currentTreeItr.next();
          String       currentTypeName = currentType.getTypeName();

          Logger.log(LOG_DEBUG, "current Type Name: " + currentTypeName);

          Iterator<String> currentNameItr = currentType.getDbObjectNames().iterator();

          while (currentNameItr.hasNext()) {
            String objName = currentNameItr.next();
            lastTree.addDBObject(currentTypeName, objName);
          }
        }

        createAllObjectsXMLFile(lastTree);
        createObjectTypeXMLFiles(lastTree);
        
      } else {
        
        createAllObjectsXMLFile(objTreeMap);
        createObjectTypeXMLFiles(objTreeMap);
      }
    } catch (Exception e) {
      Logger.logError(e);
    }
  }

  // create new all dbObjects XML file
  private void createAllObjectsXMLFile(DBObjectTree objTreeMap) {
    Logger.log(LOG_DEBUG, "createAllObjectsXMLFile...");
    XStream xstream = new XStream(new JDomDriver());

    try {
      xstream.alias("dbObjectTree", DBObjectTree.class);
      xstream.alias("dbObjectType", DBObjectType.class);
      xstream.useAttributeFor(DBObjectType.class, "typeName");
      xstream.alias("name", String.class);

      String      xmlString = xstream.toXML(objTreeMap);
      SAXBuilder  builder   = new SAXBuilder();
      InputStream is        = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
      Document    dom       = builder.build(is);
      Document    xmldoc    = new Document();

      xmldoc.addContent(getProcessInstruction("DBOBJECTS"));

      Element ele = (Element) dom.getRootElement().clone();

      xmldoc.addContent(ele);

      XMLOutputter outputter = new XMLOutputter();

      outputter.setFormat(Format.getPrettyFormat());

      File             file = getXMLOutput("DBOBJECTS");
      FileOutputStream fos  = new FileOutputStream(file);

      outputter.output(xmldoc, fos);
      fos.close();
      
      Logger.log(LOG_DEBUG, "XML file created: " + file.getAbsolutePath());

      // ALLDBObjects and MENU are the same XML file, using different XSL
      SAXBuilder  builder2 = new SAXBuilder();
      InputStream is2      = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
      Document    dom2     = builder2.build(is2);
      Document    xmldoc2  = new Document();

      xmldoc2.addContent(getProcessInstruction("MENU"));

      Element ele2 = (Element) dom2.getRootElement().clone();

      xmldoc2.addContent(ele2);

      XMLOutputter outputter2 = new XMLOutputter();

      outputter2.setFormat(Format.getPrettyFormat());

      File             file2 = getXMLOutput("MENU");
      FileOutputStream fos2  = new FileOutputStream(file2);

      outputter.output(xmldoc2, fos2);
      fos2.close();
      Logger.log(LOG_DEBUG, "XML file created: " + file2.getAbsolutePath());
      
    } catch (Exception e) {
      Logger.logError(e);
    }
  }

  // create dbObject Type XML files
  private void createObjectTypeXMLFiles(DBObjectTree objTreeMap) {
    Logger.log(LOG_DEBUG, "createObjectTypeXMLFiles...");    
    
    ArrayList<DBObjectType> types = objTreeMap.getDbObjectTypes();
    Iterator<DBObjectType>  itr   = types.iterator();

    while (itr.hasNext()) {

      // individual dbObject XML
      DBObjectType type     = itr.next();
      String       typeName = type.getTypeName();
      XStream      xstream  = new XStream(new JDomDriver());

      try {
        xstream.alias("dbObjects", DBObjectType.class);
        xstream.useAttributeFor(DBObjectType.class, "typeName");
        xstream.alias(typeName, String.class);

        String      xmlString = xstream.toXML(type);
        SAXBuilder  builder   = new SAXBuilder();
        InputStream is        = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
        Document    dom       = builder.build(is);
        Document    xmldoc    = new Document();

        xmldoc.addContent(getProcessInstruction(typeName + "S"));

        Element ele = (Element) dom.getRootElement().clone();

        xmldoc.addContent(ele);

        XMLOutputter outputter = new XMLOutputter();

        outputter.setFormat(Format.getPrettyFormat());

        File             file = getXMLOutput(typeName);
        FileOutputStream fos  = new FileOutputStream(file);

        outputter.output(xmldoc, fos);
        fos.close();
        Logger.log(LOG_DEBUG, "XML file created: " + file.getAbsolutePath());
        
      } catch (Exception e) {
        Logger.logError(e);
      }
    }
  }

  private ProcessingInstruction getProcessInstruction(String dbType) {
    ProcessingInstruction pi;

    if (dbType.equals("DBOBJECTS") || dbType.equals("MENU")) {
      pi = new ProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../XSL/" + dbType + ".XSL\"");
    } else {
      pi = new ProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"../../XSL/" + dbType + ".XSL\"");
    }

    return pi;
  }

  private File getXMLOutput(String dbType) throws Exception {
    String outputFileName = dbType + ".XML";

    String _dir = outputDir + File.separator + "DBOBJECT";

    if (!dbType.equals("DBOBJECTS") &&!dbType.equals("MENU")) {
      _dir           = _dir + File.separator + dbType.toUpperCase();
      outputFileName = dbType + "S.XML";
    }

    File dir = new File(_dir);

    if (!dir.exists()) {
      dir.mkdirs();
    }

    File file = new File(_dir, outputFileName);

    // if (file.exists()) file.delete();
    return file;
  }

  private void copyFolder(File src, File dest) throws IOException {
    if (src.isDirectory()) {

      // if directory not exists, create it
      if (!dest.exists()) {
        dest.mkdir();
      }

      // list all the directory contents
      String files[] = src.list();

      for (String file : files) {

        // construct the src and dest file structure
        File srcFile  = new File(src, file);
        File destFile = new File(dest, file);

        // recursive copy
        copyFolder(srcFile, destFile);
      }
    } else {

      // if file, then copy it
      // Use bytes stream to support all file types
      InputStream  in     = new FileInputStream(src);
      OutputStream out    = new FileOutputStream(dest);
      byte[]       buffer = new byte[1024];
      int          length;

      // copy the file content in bytes
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      in.close();
      out.close();
    }
  }
}
