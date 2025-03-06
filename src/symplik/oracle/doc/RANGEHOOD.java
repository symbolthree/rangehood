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
 * $Archive: /TOOL/RANGEHOOD/src/symplik/oracle/doc/RANGEHOOD.java $
 * $Author: Christopher Ho $
 * $Date: 30/09/10 2:00p $
 * $Revision: 4 $
 *****************************************************************************/

package symplik.oracle.doc;

//~--- non-JDK imports --------------------------------------------------------

import symplik.oracle.doc.object.DBObject;
import symplik.oracle.doc.parser.ObjectParser;
import symplik.oracle.doc.parser.ObjectParserFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.InputStream;
import java.util.*;

public class RANGEHOOD implements Constants {
  private static String profileName = "DEFAULT";

  public static void main(String[] args) {
    RANGEHOOD rh = new RANGEHOOD();

    if (args.length > 0) {
      rh.run(args[0]);
    } else {
      rh.run(profileName);
    }
  }

  private void run(String _profileName) {
    
    int noOfObject = 0;
    int counter = 0;
    
    try {
      Logger.initializeLogging();

      showVersion();
      
      Logger.log(LOG_INFO, "Java version:" + System.getProperty("java.version"));
      
      Logger.log(LOG_INFO, "Profile: " + profileName);

      System.out.println("\nProfile Name: " + profileName);
      
      Profile profile = Profile.getInstance(profileName);

      Logger.log(LOG_DEBUG, "Start to instantiate connection...");
      
      System.out.println("\nCreate database connection...");
      
      DBConnection.getInstance(profile.getDatabaseURL(_profileName), profile.getDatabaseUsername(_profileName),
                               profile.getDatabasePassword(_profileName));

      System.out.println("Database connection established.");
      
      System.out.println("\nRetrieve object list...");
      
      ArrayList<DBObject> allObjs = profile.getObjects(_profileName);
      
      noOfObject = allObjs.size();
      
      System.out.println("NO. of object to be documented: " + noOfObject);
      
      System.out.println("\nStart creating documentation...");
      
      Extractor           extract = new Extractor();
      
      System.out.println("0%-------20--------40--------60--------80------100%");
      int showedLengh = 0;
      
      for (Iterator<DBObject> itr = allObjs.iterator(); itr.hasNext(); ) {
        counter++;
        
        float precentL = 50f * Integer.valueOf(counter).floatValue() / Integer.valueOf(noOfObject).floatValue();
        int precent = (int)precentL;
        //System.out.println(precentL + "," + precent);
        for (int i=0;i<precent-showedLengh;i++) {
          System.out.print(">");
        }
        showedLengh = precent;
        
        DBObject     obj    = itr.next();
        StringBuffer sb     = extract.getContent(obj);
        ObjectParser parser = ObjectParserFactory.getParser(obj);

        parser.getODBjectInfo(obj, sb);
        parser.serializer();
      }

      System.out.println();
      System.out.println("\nProcess finished.");
      
    } catch (Exception e) {
      Logger.logError(e);
    }
  }
  
  private void showVersion() throws Exception {
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
     
     Logger.log(LOG_INFO, progName);
   
  }
 
}
