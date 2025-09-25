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

import java.io.File;

//~--- JDK imports ------------------------------------------------------------

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import symbolthree.oracle.doc.object.DBObject;
import symbolthree.oracle.doc.parser.ObjectParser;
import symbolthree.oracle.doc.parser.ObjectParserFactory;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RANGEHOOD implements Constants {

  private Options options = new Options();
  
  private static final Logger logger = LogManager.getLogger(RANGEHOOD.class.getName());

  private String profileName = "DEFAULT";
  private boolean keepXML = false; 
  private boolean keepXSL = false;

  public static void main(String[] args) {
    RANGEHOOD rh = new RANGEHOOD();
    rh.programOptions(args);
     rh.run();
  }

  private void run() {

    int noOfObject = 0;
    int counter = 0;

    try {
      showVersion();

      logger.info("Java version:" + System.getProperty("java.version"));

      logger.info("Profile: " + profileName);

      System.out.println("\nProfile Name: " + profileName);

      Profile profile = Profile.getInstance(profileName);

      logger.debug( "Start to instantiate convection...");

      System.out.println("\nCreate database connection...");

      DBConnection.getInstance(profile.getDatabaseURL(profileName), profile.getDatabaseUsername(profileName),
                               profile.getDatabasePassword(profileName));

      System.out.println("Database connection established.");

      System.out.println("\nRetrieve object list...");

      ArrayList<DBObject> allObjs = profile.getObjects(profileName);
      
      this.replaceVariableInFiles(profile);
      
      noOfObject = allObjs.size();

      System.out.println("No. of object to be documented: " + noOfObject);

      System.out.println("\nStart creating documentation...");

      Extractor extract = new Extractor();

      System.out.println("0%-------20--------40--------60--------80------100%");
      int showedLengh = 0;

      for (DBObject obj : allObjs) {
        counter++;

        float precentL = 50f * Integer.valueOf(counter).floatValue() / Integer.valueOf(noOfObject).floatValue();
        int precent = (int)precentL;
        for (int i=0;i<precent-showedLengh;i++) {
          System.out.print(">");
        }
        showedLengh = precent;

        StringBuffer sb = extract.getContent(obj);

        // only create SQL source file for some object types
        if (obj.getObjectType().equalsIgnoreCase("PROCEDURE") ||
        	obj.getObjectType().equalsIgnoreCase("FUNCTION") ||
        	obj.getObjectType().equalsIgnoreCase("PACKAGE")	) {
        
        	extract.saveSQLToFile(sb,
        		profile.getOutputDir() + File.separator +
        		"DBOBJECT" + File.separator +
        		obj.getObjectType().toUpperCase() + File.separator +
        		obj.getSchema() + "." +
        		obj.getName().toUpperCase() + ".SQL");
        }
        
        ObjectParser parser = ObjectParserFactory.getParser(obj);
        parser.getODBjectInfo(obj, sb);
        parser.serializer();
      }

      // regen dbobject and menu html
      genTemplateFiles(profile);
      
      profile.cleanup(keepXSL, keepXML);
      
      System.out.println();
      System.out.println("\nProcess finished.");

    } catch (Exception e) {
    	System.out.println(e.getMessage());
    	logger.catching(e);
    }
  }
  //https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/StringSubstitutor.html
  private void replaceVariableInFiles(Profile profile) throws Exception {
	  String newTitle = profile.getTitle();
	  logger.debug("Title = " + newTitle);

	  File file1 = new File(profile.getOutputDir(), "INDEX.HTML");
	  Transformer.replaceString(file1, "title", newTitle);
	  File file2 = new File(profile.getOutputDir() + File.separator + "HTML", "HEADER.XML");
	  Transformer.replaceString(file2, "title", newTitle);
	  File file3 = new File(profile.getOutputDir() + File.separator + "HTML", "FOOTER.XML");
	  Transformer.replaceString(file3, "title", newTitle);
	  
  }
  
  private void genTemplateFiles(Profile profile) {
	  String[] objectTypes = {"TABLE","FUNCTION","PROCEDURE","PACKAGE","VIEW","SEQUENCE","TRIGGER","MVIEW","TYPE"};
	  
	  String dbObjectFolder = profile.getOutputDir() + File.separator +	"DBOBJECT";
	  
	  Transformer transform = new Transformer();
	  transform.transform(dbObjectFolder +  File.separator + "DBOBJECTS.XML");
	  transform.transform(dbObjectFolder +  File.separator + "MENU.XML");

	  for (String type : objectTypes) {
		  File typeFile  = new File(dbObjectFolder + File.separator + type + File.separator + type + "S.XML");  
		  if (typeFile.exists()) {
			  transform.transform(typeFile.getAbsolutePath());
		  }
	  }
  }
  
  private void programOptions(String[] args) {
	  options.addOption(
			  Option.builder("profile").desc("profile name in RANGEHOOD.XML. Default profile is DEDAULT").hasArg().build());
	  options.addOption("keepxml", "Keep the data XML files. Default is NO");
	  options.addOption("keepxsl", "Keep data XSL files. Default is NO");
	  
	  if (args != null && args.length > 0 && ! args[0].startsWith("-")) {
			System.out.println("Unknown argument " + Arrays.toString(args));
			showHelp();
			System.exit(0);			
		}	  

		CommandLineParser parser = new DefaultParser();
		
		try {
		  CommandLine cmd = parser.parse(options, args);
		
		  if (cmd.hasOption("profile")) {
			profileName = cmd.getOptionValue("profile").trim();
		  }
		  
		  if (cmd.hasOption("keepxml")) {
			  keepXML = true;
		  }
		  
		  if (cmd.hasOption("keepxsl")) {
			  keepXSL = true;
		  }
		  
		} catch (ParseException e) {
			logger.error("Invalid command line arguments" + e.getCause());
		}
  }
  
  private void showHelp() {
		HelpFormatter help = new HelpFormatter();
		help.setWidth(100);
		help.printHelp("RANGEHOOD", options);		
	}  
  
  private void showVersion() {
    try {
     InputStream is = this.getClass().getResourceAsStream("/build.properties");
     Properties ver = new Properties();
     ver.load(is);
     String progName = ver.getProperty("build.product") +
                       " " + ver.getProperty("build.version") +
                       " build " + ver.getProperty("build.number");

     String border = "";

     for (int i=0;i<progName.length();i++) {
      border = border + "=";
     }

     System.out.println(border);
     System.out.println(progName);
     System.out.println(border);
     logger.info(progName);
    } catch (Exception e) {
    	logger.error("Unable to load build.properties");
    }
  }

}
