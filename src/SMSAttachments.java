import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
	TreeMap<String, String> SMSAttachments = new TreeMap<String,String> ();  //contains the hashed name and the original name
	
	public SMSAttachments(File location) throws Exception{

		Stopwatch s = new Stopwatch();
		s.start();
	
		//creates the new folder where the program will copy the files
		makeNewFolder();
		//will find the files that are needed to copy. jus the SMS attachments.
		findTheFiles(location);
		System.out.println(SMSAttachments.size());
		//copy the files by iterating through the SMSAttachments map
		copyFiles(location, SMSAttachments);
		s.stop();
		System.out.println("\nThe program took " + s.time() + " seconds to finish copying " + counter + " files to "
				+ nL.getAbsolutePath());
		
	}

	private void copyFiles(File originalLocation, TreeMap<String, String> fileNames) throws IOException, InterruptedException {
		for(Entry<String, String> entry : fileNames.entrySet()){
			String hashed = entry.getKey();
			
			//check if the hashed name from the map exist and then copy the file to the new location.
			File oldLocation = new File(originalLocation.getAbsolutePath() + "/" + hashed);
			if(oldLocation.exists()){
				counter++;
				String fileName = entry.getValue();
				String newCopy = nL.getAbsolutePath() + "/" + fileName;
				File checkNewFile = new File(newCopy);
				System.out.println("Copying " + fileName);
				if(!checkNewFile.exists()){
					//copy file
					copyFileProcess(oldLocation, checkNewFile);
				}else{
					int i = 0;
					while(checkNewFile.exists()){
						 checkNewFile = new File( nL.getAbsolutePath() + "/" + i + fileName);
						i++;
					}
					copyFileProcess(oldLocation, checkNewFile);
				}
			}
		}	
	}

	private void copyFileProcess(File oldLocation, File checkNewFile) throws IOException, InterruptedException {
		String[] cmds = { "rsync","-a", oldLocation.toString(), checkNewFile.toString()};
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
	}

	private void findTheFiles(File location) throws Exception {
		FileLister findFiles = new FileLister(location); //the fileInfoList contains the hashed named and the
		
		for(Entry<String, String> entry : findFiles.fileInfoList.entrySet()){
			String hashedName = entry.getKey();
			String originalName = nameOfFile(entry.getValue()); //get the file name from the path name
			SMSAttachments.put(hashedName, originalName); //adds it to the smsmattachments map. the hashed name and the original name.
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