import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
/**
*A simple program that will copy all the SMS attachments from your iPhone/iPod/iPad back up.
*Hashing.java is code found online, and FileLister and FileInfo are by @author Roy van Rijn and using it with permission.
* SMSAttachments.
* There are still a few things to add, for example what if there arent any folders, and no plist file, and also the scanner input.
* My GitHub https://github.com/leosanchez16/
* @author Luis Sanchez @leosanchez16 
*/

public class MainRunner {

	public static void main(String[] args) throws Exception{
		
		Scanner n = new Scanner(System.in);
		String locBackup = System.getProperty("user.home") + "/Library/Application Support/MobileSync/Backup/";
		File[] folder = new File(locBackup).listFiles();
		System.out.println("Please select the device that you would like to extract the attachments: ");
		//find a way to have three categories? in a treemap
		TreeMap<String, File> names = allDevices(folder); //and if statement if there arent any folders to look into
		TreeMap<Integer, String> deviceWithNumber = new TreeMap<Integer,String> ();
		int num = 1;
		for(Entry<String, File> entry : names.entrySet()) {
			  String deviceName = entry.getKey();
			  deviceWithNumber.put(num, deviceName);
			  System.out.println( num + ". " + deviceName);
			  num++;
		}
		
		//ADD TO CHECK IF THE MANIFEST FILE IS IN THE THAT FOLDER? OR ADDED TO THE METHOD BELOW AND DONT ADD THE DEVICE NAME?
		//options to delete the files from the backup
		boolean foundIt = false;
		String deviceName = null;
		while(!foundIt){
			int key = n.nextInt(); //what about when the user inputs a character? it throws an error
			
			if(deviceWithNumber.containsKey(key)){
				foundIt = true;
				deviceName = deviceWithNumber.get(key); 
				System.out.println("Thank you. The program will do the rest. Just sit back and relax :)");
			}else{
				System.out.println("Please try again. Select one of the option above.");
			}
		}
		new SMSAttachments(names.get(deviceName));
		
		
		
		
	}
	
	public static TreeMap<String, File> allDevices(File[] folder){
		
		//look into the backup folders to find the info.plist file
		TreeMap<String, File> deviceNames = new TreeMap<String,File> ();
		String deviceName = null;

		    for (File file : folder) {
		        if (file.isDirectory()) {
		        	deviceName = new ReadXMLFile().findName(file.getPath() + "/Info.plist");	        	
		        	deviceNames.put(deviceName, file); 
		        }
		    }
		
		return deviceNames;
		
	}
}
