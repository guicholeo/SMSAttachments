import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
 
public class ReadXMLFile {
 
public String findName(String location){
	 String deviceName = null;
   try {
 
	File fXmlFile = new File(location);
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	dbFactory.setValidating(false);
	dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
 
	//optional, but recommended
	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	doc.getDocumentElement().normalize();
 
	NodeList nList = doc.getElementsByTagName("string");
	Node nNode = nList.item(1); //to get the key called device name
	deviceName =  nNode.getTextContent();
    } catch (Exception e) {
    	e.printStackTrace();
    }
	return deviceName;
 }
 
}