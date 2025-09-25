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


package symbolthree.oracle.doc;

//~--- JDK imports ------------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.ProcessingInstruction;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

//~--- non-JDK imports --------------------------------------------------------

import com.thoughtworks.xstream.XStream;

import symbolthree.oracle.doc.object.DBObject;
import symbolthree.oracle.doc.object.DBObjectTree;
import symbolthree.oracle.doc.object.DBObjectType;

public class Profile implements Constants {
  private static Profile  myProfile = null;
  private static Document document  = null;

  private String          outputDir = null;
  private String          title = "Oracle Database Object Documentation";  // default title if not specified
  private File            profileFile;
  private static String   PROFILE_FILENAME = "RANGEHOOD.XML";

  private XPathFactory    xpfac    = XPathFactory.instance();

  private static String      ATTRIBUTE_NODE = "A";
  private static String      ELEMENT_NODE   = "E";

  private ArrayList<DBObject> allObjects = new ArrayList<>();

  static final Logger logger = LogManager.getLogger(Profile.class.getName());  
  
  protected Profile(String profileName) throws Exception {

      SAXBuilder builder = new SAXBuilder();

      profileFile = new File(System.getProperty("user.dir") + File.separator + PROFILE_FILENAME);
      logger.debug("Loading " + profileFile.getAbsolutePath() + "...");
      document = builder.build(profileFile);
      logger.debug(profileFile.getAbsolutePath() + " loaded.");

      List<Element> list = getListfromXPath("/RANGEHOOD/PROFILE[@name='" + profileName + "']");

      if (list.size() == 0) {
        logger.error("Profile " + profileName + " not found.");
        throw new Exception("Profile " + profileName + " not found.");
      } else {
        setOutputDir(profileName);
        setTitle(profileName);
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

  public void setTitle(String profileName) throws Exception {
	  String _title = getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/TITLE", ELEMENT_NODE); 
	  this.title = _title==null?title:_title; 
  }
  
  public String getTitle() {
	  return this.title;
  }
  
  public String getOutputDir() {
    return outputDir;
  }

  private void setOutputDir(String profileName) throws Exception {

    String dir = null;
    String attrVal = null;

    if (outputDir==null) {

      attrVal = getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/@output", ATTRIBUTE_NODE);
      File file = new File(attrVal);
      if (file.exists() && file.isDirectory()) {
        dir = file.getAbsolutePath();
      }

      if (! file.exists()) {
    	 file.mkdir(); 
    	 dir = file.getAbsolutePath();
    	 logger.info("Create folder " + file.getAbsolutePath()); 
      }

    } else {
      dir = outputDir;
    }

    if (dir==null) {
      throw new Exception ("Unable to find directory : " + attrVal);
    } else {
    	logger.debug("Output dir=" + dir);
      outputDir = dir;
    }
  }

  private boolean isAppendMode(String profileName) throws Exception {
    //xpath = XPath.newInstance("/RANGEHOOD/PROFILE[@name='" + profileName + "']/@append");
    //Attribute attr = (Attribute) xpath.selectSingleNode(document);
    //String    str  = attr.getValue().toUpperCase();

    String str = getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/@append", ATTRIBUTE_NODE);
    if (str.toUpperCase().equals("Y")) {
      return true;
    } else {
      return false;
    }
  }

  public String getDatabaseURL(String profileName) throws Exception {
    return getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/DATABASE/URL", ELEMENT_NODE);
  }

  public String getDatabaseUsername(String profileName) throws Exception {
    return getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/DATABASE/USERNAME", ELEMENT_NODE);

  }

  public String getDatabasePassword(String profileName) throws Exception {
    return getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/DATABASE/PASSWORD", ELEMENT_NODE);

  }
  
  public String getTitle(String profileName) throws Exception {
	  return getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/TITLE", ELEMENT_NODE);
  }

  public ArrayList<DBObject> getObjects(String profileName) throws Exception {
    DBObjectTree objTreeMap = new DBObjectTree();
    //String       schema     = getDatabaseUsername(profileName);
    Connection   conn       = DBConnection.getInstance().getConnection();

    // get all distinct object type
    List<Element> list = getListfromXPath("/RANGEHOOD/PROFILE[@name='" + profileName + "']/DBOBJECT");

    String sql = "SELECT DECODE(VIEW1.OBJECT_TYPE, 'MATERIALIZED VIEW', 'MVIEW', VIEW1.OBJECT_TYPE) OBJECT_TYPE" +
    		     "     , VIEW1.OBJECT_NAME " +
    		     "     , VIEW1.OWNER FROM " +
                 "(SELECT OBJECT_TYPE, OBJECT_NAME, OWNER FROM ALL_OBJECTS WHERE 1=0";

    for (Element ele : list) {
      String owner         = ele.getAttributeValue("owner").toUpperCase();    	
      String objType       = ele.getAttributeValue("type").toUpperCase();
      String objName       = ele.getAttributeValue("name") ;
      String include       = ele.getAttributeValue("include");
      
      if (objType.equals("MVIEW")) objType="MATERIALIZED VIEW";
//      if (objType.equals("PACKAGE-SPEC")) objType="PACKAGE";
//      if (objType.equals("PACKAGE-BODY")) objType="PACKAGE BODY";
      if (objType.equals("ALL"))   objType="%";

      if (include.equals("Y")) {
        sql = sql + " UNION ";
      } else if (include.equals("N")) {
        sql = sql + " MINUS ";
      }

      sql = sql + "SELECT A.OBJECT_TYPE" +
 		          "     , A.OBJECT_NAME" +
    		      "     , A.OWNER" +
 		          "  FROM ALL_OBJECTS A" +
                  " WHERE A.OBJECT_TYPE LIKE '" + objType + "'" +
                  "   AND A.OBJECT_NAME LIKE '" + objName + "'" + 
                  "   AND A.OWNER='" + owner + "'";

      // if search for Table, eliminate Materialized View (which is shown as TABLE in xxx_objects)
      if (objType.equals("TABLE")) {
        sql = sql + " AND NOT EXISTS (SELECT 'X' FROM ALL_OBJECTS B WHERE"
              + " B.OBJECT_TYPE='MATERIALIZED VIEW' AND A.OBJECT_NAME=B.OBJECT_NAME AND A.OWNER='" + owner + "')";
      }
    }

    sql = sql + ") VIEW1 WHERE VIEW1.OBJECT_TYPE IN (" +
    		        "'TABLE','FUNCTION','PROCEDURE','PACKAGE','VIEW','SEQUENCE','TRIGGER','MATERIALIZED VIEW','TYPE') " +
    		        " AND VIEW1.OBJECT_NAME NOT LIKE '%=' " + 
    		        " AND VIEW1.OBJECT_NAME NOT LIKE '%+' " +
    		        " AND VIEW1.OBJECT_NAME NOT LIKE '%#' " +
    		        " ORDER BY VIEW1.OWNER, VIEW1.OBJECT_TYPE, VIEW1.OBJECT_NAME";

    logger.debug("SQL : " + sql);

    ResultSet rs = conn.createStatement().executeQuery(sql);

    while (rs.next()) {

      String objType = rs.getString(1);
      String objName = rs.getString(2);
      String objSchema  = rs.getString(3);
      objTreeMap.addDBObject(objType, objName, objSchema);
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

      while (itr2.hasNext() && ! typeName.equals("Package body")) {
        Class<?>    clazz = Class.forName("symbolthree.oracle.doc.object." + typeName);
        DBObject obj   = (DBObject) clazz.getDeclaredConstructor().newInstance();

        String fullName = itr2.next();
        
        obj.setSchema(fullName.split("\\.")[0]);
        obj.setName(fullName.split("\\.")[1]);        
        obj.setObjectType(typeName);
        allObjects.add(obj);
        logger.info("Create dbObject " + obj.getObjectType() + ": " + obj.getSchema() + "." + obj.getName());
      }
    }

    boolean appendMode = this.isAppendMode(profileName);

    if (!appendMode) {
      String templateName = getSingleXPathValue("/RANGEHOOD/PROFILE[@name='" + profileName + "']/@template", ATTRIBUTE_NODE);
      File   srcFolder    = new File(System.getProperty("user.dir") + File.separator + "template" + File.separator
                                        + templateName);
      if (!srcFolder.exists()) {
        throw new Exception("Template directory not found:" + srcFolder.getAbsolutePath());
      }
      copyFolder(srcFolder, new File(getOutputDir()));
    }

    logger.debug("DBObject Type Size: " + objTreeMap.getDbObjectTypes().size());

    createAllObjectsXMLFile(objTreeMap, appendMode);

    return allObjects;
  }

  private void createAllObjectsXMLFile(DBObjectTree objTreeMap, boolean appendMode) {
    if (appendMode) {
   	  logger.debug("Append=YES");
      appendAllObjectsXMLFile(objTreeMap);
    } else {
      logger.debug("Append=NO");
      createAllObjectsXMLFile(objTreeMap);
      createObjectTypeXMLFiles(objTreeMap);
    }
  }

  // append to dbObjects XML file
  private void appendAllObjectsXMLFile(DBObjectTree objTreeMap) {
    XStream xstream = new XStream();

    xstream.alias("dbObjectTree", DBObjectTree.class);
    xstream.alias("dbObjectType", DBObjectType.class);
    xstream.useAttributeFor(DBObjectType.class, "typeName");
    xstream.alias("name", String.class);

    try {
      File file = getXMLOutput("DBOBJECTS");

      logger.debug("Check DBOBJECTS file: " + file.getAbsoluteFile() + " - " + file.exists()); 

      if (file.exists()) {
        InputStream  is       = new FileInputStream(file);
        DBObjectTree lastTree = (DBObjectTree) xstream.fromXML(is);

        logger.debug("done convert xml to object");

        Iterator<DBObjectType> currentTreeItr = objTreeMap.getDbObjectTypes().iterator();

        while (currentTreeItr.hasNext()) {
          DBObjectType currentType     = currentTreeItr.next();
          String       currentTypeName = currentType.getTypeName();

          logger.debug("current Type Name: " + currentTypeName);

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
      logger.catching(e);
    }
  }

  // create new all dbObjects XML file
  private void createAllObjectsXMLFile(DBObjectTree objTreeMap) {
	logger.debug("createAllObjectsXMLFile...");
    XStream xstream = new XStream();

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

      Element ele = dom.getRootElement().clone();

      xmldoc.addContent(ele);

      XMLOutputter outputter = new XMLOutputter();

      outputter.setFormat(Format.getPrettyFormat());

      File             file = getXMLOutput("DBOBJECTS");
      FileOutputStream fos  = new FileOutputStream(file);

      outputter.output(xmldoc, fos);
      fos.close();

      logger.debug("XML file created: " + file.getAbsolutePath());

      // ALLDBObjects and MENU are the same XML file, using different XSL
      SAXBuilder  builder2 = new SAXBuilder();
      InputStream is2      = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
      Document    dom2     = builder2.build(is2);
      Document    xmldoc2  = new Document();

      xmldoc2.addContent(getProcessInstruction("MENU"));

      Element ele2 = dom2.getRootElement().clone();

      xmldoc2.addContent(ele2);

      XMLOutputter outputter2 = new XMLOutputter();

      outputter2.setFormat(Format.getPrettyFormat());

      File             file2 = getXMLOutput("MENU");
      FileOutputStream fos2  = new FileOutputStream(file2);

      outputter.output(xmldoc2, fos2);
      fos2.close();
      logger.debug("XML file created: " + file2.getAbsolutePath());

    } catch (Exception e) {
      logger.catching(e);
    }
  }

  // create dbObject Type XML files
  private void createObjectTypeXMLFiles(DBObjectTree objTreeMap) {
	logger.debug("createObjectTypeXMLFiles...");

    ArrayList<DBObjectType> types = objTreeMap.getDbObjectTypes();
    Iterator<DBObjectType>  itr   = types.iterator();

    while (itr.hasNext()) {

      // individual dbObject XML
      DBObjectType type     = itr.next();
      String       typeName = type.getTypeName();
      XStream      xstream  = new XStream();

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

        Element ele = dom.getRootElement().clone();

        xmldoc.addContent(ele);

        XMLOutputter outputter = new XMLOutputter();

        outputter.setFormat(Format.getPrettyFormat());

        File             file = getXMLOutput(typeName);
        FileOutputStream fos  = new FileOutputStream(file);

        outputter.output(xmldoc, fos);
        fos.close();
        logger.debug("XML file created: " + file.getAbsolutePath());

      } catch (Exception e) {
        logger.catching(e);
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
	  FileUtils.copyDirectory(src, dest);
  }
  
  public void cleanup(boolean keepXSL, boolean keepXML) throws IOException {
	if (! keepXSL) {
		logger.info("cleanup XSL files..");		
		File xslFolder = new File(outputDir, "XSL");
		FileUtils.deleteDirectory(xslFolder);
	}
		
	if (! keepXML) {
		logger.info("cleanup XML files..");	
	    Collection<File> xmlFiles = FileUtils.listFiles(
	    		new File(outputDir),
	            new String[]{"XML"}, 
	            true);
	    for (File file : xmlFiles) {
	        try {
	            FileUtils.forceDelete(file);
	        } catch (IOException e) {
	            logger.warn("Unable to delete " + file.getAbsolutePath());
	        }
	    }    
	}
  }

  private List<Element> getListfromXPath(String xpath) {
	  XPathExpression<Element> xp = xpfac.compile(xpath, Filters.element());
	  List<Element> eleList = xp.evaluate(document);
	  return eleList;
  }


  private String getSingleXPathValue(String xpath, String nodeType) {
      String rtnVal = null;

      if (nodeType.equals(ATTRIBUTE_NODE)) {
          XPathExpression<Attribute> xp   = xpfac.compile(xpath, Filters.attribute());
          Attribute                  attr = xp.evaluateFirst(document);

          if (attr != null) {
              rtnVal = attr.getValue();
          }
      }

      if (nodeType.equals(ELEMENT_NODE)) {
          XPathExpression<Element> xp  = xpfac.compile(xpath, Filters.element());
          Element                  ele = xp.evaluateFirst(document);

          if (ele != null) {
              rtnVal = ele.getValue();
          }
      }

      return rtnVal;
  }
}
