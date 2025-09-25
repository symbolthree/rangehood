package symbolthree.oracle.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.ProcessingInstruction;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.transform.XSLTransformer;
//https://www.jdom.org/docs/apidocs/org/jdom2/transform/XSLTransformer.html

public class Transformer implements Constants {

	private String xmlPath;

    private XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
    private SAXBuilder builder = new SAXBuilder();

    static final Logger logger = LogManager.getLogger(Transformer.class.getName());  
    
    public void transform(String _xmlPath) {
    	this.setXmlPath(_xmlPath);
    	this.transform();
    }
	
	public void transform() {
        try {
        	File xmlFile       = new File(xmlPath);
        	String xmlDir      = xmlFile.getParent();
        	String xmlFileName = xmlFile.getName();
        	
        	File htmlFile = new File(xmlDir, xmlFileName.replaceAll("\\.XML", "\\.HTML")); 
    		
            Document xmlDoc = builder.build(xmlFile);
            
            String xslDocPath = getXslHref(xmlDoc);
            
            Document xslDoc = builder.build(new File(xmlDir, xslDocPath));
            
            XSLTransformer transformer = new XSLTransformer(xslDoc);

            Document htmlDoc = transformer.transform(xmlDoc);

            FileOutputStream fos = new FileOutputStream(htmlFile);
            xmlOutput.output(htmlDoc, fos);
            fos.close();

            logger.info("html created: " + htmlFile.getAbsolutePath());

        } catch (Exception e) {
            logger.catching(e);
        }
	}
	
	private String getXslHref(Document doc) {
      String hrefValue = null;		
      try {
        for (Content content : doc.getContent()) {
          if (content instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) content;
            if (pi.getTarget().equals("xml-stylesheet")) {
              hrefValue = pi.getPseudoAttributeValue("href");
              break; // Found the xml-stylesheet PI, no need to continue
            }
          }
        }
      } catch (Exception e) {
        logger.catching(e);
      }
      return hrefValue;
	}

	public String getSetXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	
	public static void replaceString(File _file, String _fromText, String _toText) throws IOException {
		
	  Map<String, String> valuesMap = new HashMap<>();
	  valuesMap.put(_fromText, _toText);
	  StringSubstitutor sub = new StringSubstitutor(valuesMap);
	  
	  logger.debug("replace string in file " + _file.getAbsolutePath());
	  String fileAsText = new String(Files.readAllBytes(_file.toPath()));
	  String replacedText = sub.replace(fileAsText);
	  
	  sub = StringSubstitutor.createInterpolator();
	  replacedText = sub.replace(replacedText);	  
	  
	  FileWriter fileWriter = new FileWriter(_file);
	  fileWriter.write(replacedText);
	  fileWriter.close();
	}
}
