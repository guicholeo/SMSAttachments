import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.TreeMap;
/**
* SMSAttachemnts.
* @author Luis Sanchez @leosanchez16 
*/
public class SMSAttachments {

	//make it run from the CopyFiles file
	private String original = "SMSAttachments";
	private int counter = 0;
	private String newLocation = "";
	private File nL;
	String results;
	private TreeMap<String, FileInfo> SMSAttachments = new TreeMap<String,FileInfo> ();  //contains the hashed name and the original name
	
	public SMSAttachments(File location) throws Exception{
		Stopwatch s = new Stopwatch();
		s.start();
		//creates the new folder where the program will copy the files
		makeNewFolder();
		//will find the files that are needed to copy. just the SMS attachments. make an option if it wants other files
		findTheFiles(location);
		//copy the files by iterating through the SMSAttachments map
		copyFiles(location, SMSAttachments);
		s.stop();
		
		System.out.println("\nThe program took " + s.time() + " seconds to finish copying " + counter + " files to "
				+ nL.getAbsolutePath());
		System.out.println("Follow me on Twitter @leosanchez16");
		
	}

	private void copyFiles(File originalLocation, TreeMap<String, FileInfo> hashedWithInfo) throws IOException, InterruptedException {
		FileInfo fileProp;
		for(Entry<String, FileInfo> entry : hashedWithInfo.entrySet()){
			String hashed = entry.getKey();
			
			//check if the hashed name from the map exist and then copy the file to the new location.
			File oldLocation = new File(originalLocation.getAbsolutePath() + "/" + hashed);
			fileProp = entry.getValue();
			if(oldLocation.exists()){
				counter++;
				String fileName = fileProp.getOriginalName();
				String newCopy = nL.getAbsolutePath() + "/" + fileName;
				File checkNewFile = new File(newCopy);
				
				if(!checkNewFile.exists()){
					//copy file
					System.out.println("Copying " + fileName);
					copyFileProcess(oldLocation, checkNewFile, fileProp);
				}else{
					int i = 0;
					while(checkNewFile.exists()){
						 checkNewFile = new File( nL.getAbsolutePath() + "/" + i + fileName);
						 //later a method to add duplicates like duplicate-1.PNG?
						i++;
					}
					System.out.println("Copying " +i + fileName);
					copyFileProcess(oldLocation, checkNewFile, fileProp);
				}
			}
		}	
	}

	private void copyFileProcess(File oldLocation, File newFileLocation, FileInfo fileProp) throws IOException, InterruptedException {
		String[] cmds = { "rsync","-a", oldLocation.toString(), newFileLocation.toString()};
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
		int createdTime = fileProp.getCtime();
		int modTime = fileProp.getMtime();
		if(createdTime == modTime){
			String cmdType = "-t"; // will modify both C and M time
			applyProperties(createdTime, cmdType, newFileLocation);
		}else{
			String cmdType = "-t"; //created
			applyProperties(createdTime, cmdType, newFileLocation);
			 cmdType = "-mt"; //modified
			applyProperties(modTime, cmdType, newFileLocation);
		}
		
	}

	private void applyProperties(int time, String cmdType, File newFileLocation) throws IOException, InterruptedException {
		
		//adds the CTime and MTime
		long unixSeconds = time;
		Date date = new Date(unixSeconds*1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm.ss");
		//sdf.setTimeZone(TimeZone.getTimeZone("GMT-6"));
		String formatedDate = sdf.format(date);
		String[] cmds = { "touch", cmdType ,formatedDate, newFileLocation.toString()};
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
		
	}

	private void findTheFiles(File location) throws Exception {
		FileLister findFiles = new FileLister(location); //the map fileInfoList contains the hashed named and the FileInfo with all the 
		//the properties
		
		for(Entry<String, FileInfo> entry : findFiles.fileInfoList.entrySet()){
			String hashedName = entry.getKey();
			FileInfo theInfoFile = entry.getValue();
			String originalName = nameOfFile(theInfoFile.getFilename()); //get the file name from the path name
			String videoMovNewName = entry.getValue().getProperties().get("com.apple.assetsd.originalFilename"); //gets the original file name for the video
			if (originalName.contains("MOV_") && videoMovNewName != null){
				originalName = videoMovNewName;
			}
			//to add the creating and modified time i need to create a list file like file info and filelister  or just use that one
			theInfoFile.setOriginalName(originalName);
			SMSAttachments.put(hashedName, theInfoFile); //adds it to the smsmattachments map. the hashed name and the original name.
		}
		
	}

	private String nameOfFile(String value) {
		
		//traverses the array of string backwards until it hits the "/" and stops. what if the file name contains the "/"?
		ArrayList<String> nameOfTheFile = new ArrayList<String>();
		for(int i =value.length() - 1; i >= 0; i--){
			char character = value.charAt(i);
			if(character != '/'){
				nameOfTheFile.add("" + character);
			}else{
				break;
			}
		}
		
		return file(nameOfTheFile);
	}
	
	//flips the array? is there a method already built in?
	private String file(ArrayList<String> r) {
		String newName = "";
		for(int i = r.size()-1; i >= 0; i--){
			newName += r.get(i);
		}
		return newName;
	}

	private void makeNewFolder() {
		newLocation = System.getProperty("user.home") + "/Desktop/" + original;
		int loc = 1;
		nL = new File(newLocation);
		while(!nL.mkdir()){
			nL = new File(System.getProperty("user.home") + "/Desktop/" + original + loc);
			loc++;
		}
		
	}
}