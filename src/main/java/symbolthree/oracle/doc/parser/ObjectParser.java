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



package symbolthree.oracle.doc.parser;

//~--- JDK imports ------------------------------------------------------------

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//~--- non-JDK imports --------------------------------------------------------

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.ProcessingInstruction;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import symbolthree.oracle.doc.Constants;
import symbolthree.oracle.doc.Profile;
import symbolthree.oracle.doc.Transformer;
import symbolthree.oracle.doc.object.Comment;
import symbolthree.oracle.doc.object.DBObject;
import symbolthree.oracle.doc.object.Tag;

public class ObjectParser implements Constants {


  static final Logger logger = LogManager.getLogger(ObjectParser.class.getName());  
/*
   * Stub method
   */
  public DBObject getODBjectInfo(DBObject dbObj, StringBuffer sb) {
    return null;
  }

  public static StringBuffer stripeCRLF(StringBuffer sb) {
    StringBuffer out = new StringBuffer();

    for (int i = 0; i < sb.length(); i++) {
      out.append((sb.charAt(i) == '\n')
                 ? ' '
                 : sb.charAt(i));
    }

    return out;
  }

  public static ArrayList<String> convertToLines(StringBuffer sb) {
    ArrayList<String> allLines = new ArrayList<>();
    StringBuffer      line     = new StringBuffer();

    for (int i = 0; i < sb.length(); i++) {
      char ch = sb.charAt(i);

      if (ch == '\n') {
        if (line.toString().trim().length() > 0) {
          allLines.add(line.toString().trim());
        }

        line.setLength(0);
      } else {
        line.append(sb.charAt(i));
      }
    }

    return allLines;
  }

  /**
   * Common method to extract comment from package, procedure, function, etc.
   * @param allLines original source stored in string array
   * @return  Comment object
   */
  public Comment getComment(ArrayList<String> allLines) {
    StringBuffer sb      = new StringBuffer();
    Comment      comment = new Comment();

    // scan for the first /** and **/
    int startLine = 0;
    int endLine   = 0;

    for (int i = 0; i < allLines.size(); i++) {
      
      if (allLines.get(i).startsWith("/*") && allLines.get(i).endsWith("*/")) {
    	  startLine = i;
    	  endLine = i;
    	  break;
      }

      if (allLines.get(i).startsWith("/*")) {
        startLine = i;
      } else if (allLines.get(i).endsWith("*/")) {
        endLine = i;
        break;
      }
    }

    for (int i = startLine + 1; i < endLine; i++) {
      String line = allLines.get(i);

      line = line.replace('*', ' ').trim();

      if (line.startsWith("@")) {
        Tag             tag     = new Tag();
        StringTokenizer st      = new StringTokenizer(line);
        String          tagName = st.nextToken();

        tagName = tagName.substring(1, tagName.length());
        tag.setName(tagName);

        if (tagName.equals("param")) {
          tag.setValue(st.nextToken());
          tag.setComment(st.nextToken("*").trim());
        } else {
          tag.setValue(st.nextToken("*").trim());
        }

        comment.addTag(tag);
      } else {
        sb.append(line + "\n");
      }
    }

    comment.setContent(sb.toString());

    return comment;
  }

  /**
   * return XML process instruction (XSL file) of this dbObject
   * @param dbObj DBObject
   * @return
   */
  public ProcessingInstruction getProcessInstruction(DBObject dbObj) {
    ProcessingInstruction pi = new ProcessingInstruction("xml-stylesheet",
                                 "type=\"text/xsl\" href=\"../../XSL/" + dbObj.getObjectType().toUpperCase()
                                 + ".XSL\"");

    return pi;
  }

  public File getXMLOutput(DBObject dbObj) {
    String objectType = dbObj.getObjectType().toUpperCase();
    String objectName = dbObj.getSchema()+ "." + dbObj.getName().toUpperCase();
    String outputDir  = Profile.getInstance().getOutputDir();
    outputDir = outputDir + File.separator + "DBOBJECT" + File.separator + objectType;
    File dir = new File(outputDir);

    if (!dir.exists()) {
      dir.mkdir();
    }

    File file = new File(outputDir, objectName + ".XML");

    if (file.exists()) {
      file.delete();
    }

    return file;
  }

  /**
   * stub method for other DBObjects
   */
  public void serializer() {}

  /**
   * Common method to write the final DBObject XML file
   * @param dbObj      DBObject object
   * @param xmlString  String created by XStream marshaller
   * @throws Exception
   */
  public void writeXMLOutput(DBObject dbObj, String xmlString) throws Exception {
    SAXBuilder  builder = new SAXBuilder();
    InputStream is      = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
    Document    dom     = builder.build(is);
    Document    xmldoc  = new Document();

    xmldoc.addContent(getProcessInstruction(dbObj));

    Element ele = dom.getRootElement().clone();

    xmldoc.addContent(ele);

    XMLOutputter outputter = new XMLOutputter();

    outputter.setFormat(Format.getPrettyFormat());

    File             file = getXMLOutput(dbObj);
    FileOutputStream fos  = new FileOutputStream(file);

    logger.info("Writing XML " + file.getAbsolutePath());
    outputter.output(xmldoc, fos);
    fos.close();
    
    // write html file
    logger.info("Writing HTML " + file.getAbsolutePath());
    Transformer transformer = new Transformer();
    transformer.setXmlPath(file.getAbsolutePath());
    transformer.transform();
  }
  

  public static String stripNonValidXMLCharacters(String str) {
    str = str.replace("\u0000", "");

    if (str == null) {
      return null;
    }

    StringBuffer s = new StringBuffer();

    for (char c : str.toCharArray()) {
      if ((c == 0x9) || (c == 0xA) || (c == 0xD) || ((c >= 0x20) && (c <= 0xD7FF)) || ((c >= 0xE000) && (c <= 0xFFFD))
          || ((c >= 0x10000) && (c <= 0x10FFFF))) {
        s.append(c);
      }
    }

    return s.toString();
  }
}
