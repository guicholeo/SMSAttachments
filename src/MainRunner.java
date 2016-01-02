import java.io.File;

import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
/**
* A simple program that will copy all the SMS attachments from your iPhone/iPod/iPad back up.
* Hashing.java is code found online, and FileLister and FileInfo are by @author Roy van Rijn and using it with permission.
* SMSAttachments. The files will contain the original creation and modification times.
* ?try to sort them by phone number?
* My GitHub https://github.com/leosanchez16/
* @author Luis Sanchez @leosanchez16 
*/

public class MainRunner {

	public static void main(String[] args) throws Exception{
		
		
		String locBackup = System.getProperty("user.home") + "/Library/Application Support/MobileSync/Backup/";
		File[] folder = new File(locBackup).listFiles();
		//find a way to have three categories? in a treemap
		TreeMap<String, File> namesofDevices = allDevices(folder);
		//checks if there arent any folders to check.
		//maybe find a way to back up a device if its connected?
		if(namesofDevices.isEmpty()){
			System.out.println("There aren't any backup files to check. Please back up a device and run this program again.");
			System.exit(1);
		}
		System.out.println("Please select the device that you would like to extract the attachments from:");
		TreeMap<Integer, String> deviceWithNumber = new TreeMap<Integer,String> ();
		int num = 1;
		for(Entry<String, File> entry : namesofDevices.entrySet()) {
			  String deviceName = entry.getKey();
			  //if there are more than two back ups and one doesnt have a Manifest.mbdb the string is null, it doesnt print the device name.
			  if(!(deviceName == null)){
				  deviceWithNumber.put(num, deviceName);
				  System.out.println( num + ". " + deviceName);
				  num++;
			  }
		}
		
		//options to delete the files from the backup
		boolean foundIt = false;
		String deviceName = null;
		Scanner n = new Scanner(System.in);
		while(!foundIt){
			n = new Scanner(System.in);
			//checks if the input is a number or character
			if(n.hasNextInt()){
				int key = n.nextInt(); 
				if(deviceWithNumber.containsKey(key)){
					/*more options to the extraction from the device
					1. keep files  in backup
					2. delete files from back up, to make space in phone only non jailbroken. it also creates a back up of the attachments just in case you want them in the future, r
					remembers the location
						2.a ARE YOU SURE, IT WILL DELETE THE FILES FROM THE BACKUP
							2.a.a do you want to delete them completely from the backup folder? they will still be in your extracted folder
					a find a way to restore without manually restoring?*/
					foundIt = true;
					deviceName = deviceWithNumber.get(key); 
					System.out.println("Thank you. The program will do the rest. Just sit back and relax :)");
				}else{
					System.out.println("Please try again. Select one of the option above.");
				}
			}else{
				System.out.println("Please enter a number from the list above.");
			}
			
		}
		new SMSAttachments(namesofDevices.get(deviceName));
		
	}
	
	public static TreeMap<String, File> allDevices(File[] folder){
		
		//look into the backup folders to find the info.plist file
		TreeMap<String, File> deviceNames = new TreeMap<String,File> ();
		String deviceName = null;
			
			    for (File file : folder) {
			    	//if Manifest.mbdb is not present it doesnt continue
			    	if(new File(file.getPath()+"/Manifest.mbdb").exists()){
			    		if (file.isDirectory()) {
			    			deviceName = new ReadXMLFile().findName(file.getPath() + "/Info.plist");
			    			deviceNames.put(deviceName, file); 
			    		}
			    	}
			    }
		return deviceNames;
		
	}
}
