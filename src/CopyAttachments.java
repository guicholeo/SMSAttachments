import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.JOptionPane;
/**
* CopyAttachemnts.
* @author Luis Sanchez @leosanchez16 
*/
public class CopyAttachments {

	//make it run from the CopyFiles file
	private String original = "CopyAttachments";
	private int counter = 0;
	private String newLocation;
	//private JTextArea text;
	private String newLine = "\n";
	private File nL;
	String results;
	
	public CopyAttachments(File location) throws Exception{

		Stopwatch s = new Stopwatch();
		s.start();
		newLocation = System.getProperty("user.home") + "/Desktop/" + original + "/";
		int loc = 0;
		nL = new File(newLocation);
		while(!nL.mkdir()){
			nL = new File(System.getProperty("user.home") + "/Desktop/" + original + loc);
			loc++;
		}
		FileLister findFiles = new FileLister(location);
		TreeMap<String, String> SMSAttachments = new TreeMap<String,String> ();
		
		for(Entry<String, FileInfo> entry : findFiles.fileInfoList.entrySet()) {
			  String key = entry.getKey();
			  FileInfo value = entry.getValue();
			  if(value.getFilename().contains("Library/SMS/Attachments/") && 
					  !value.getFilename().contains("preview" ) && !value.getFilename().contains(".DS_Store")){ //and the ds.Store file...
				  String originalFileName = nameOfFile(value.getFilename());
				  SMSAttachments.put(key, originalFileName);
			  }
		}
		
		//make it run from copyFiles.
		copythefiles(location, SMSAttachments);
		s.stop();
		results = "CopyAttachemnts took " + s.time() + " seconds to copy " + counter + " files";
		OpenFolderButton.text.append(results);
		JOptionPane.showMessageDialog(null, results + "\n Follow me on Twitter @leosanchez16");
	}
	
	
	
	private void copythefiles(File location, TreeMap<String, String> justImages) throws IOException, InterruptedException {
		//rsync -a SourceDirectoryPath DestinationDirectoryPath 

		
		if(location.isDirectory()){
			for(File oldLocation: location.listFiles()){
				String fileName = oldLocation.getName();
				if(justImages.containsKey(fileName)){
					String newFileName = justImages.get(fileName);
					Process p =null;
					//File tempLoc = new File(newLocation);	
					String newLocWithSlash = nL.toString() + "/";
					File tempName = new File(newLocWithSlash + newFileName);	
					//System.out.println("rsync -a " + oldLocation.toString() + " " + nL.toString() +"/");
					
					String[] cmds = { "rsync","-a", oldLocation.toString(), newLocWithSlash};
					if(!tempName.exists()){
						OpenFolderButton.text.append("Copying " + fileName + newLine);
						 p = Runtime.getRuntime().exec(cmds);						
						p.waitFor();
						OpenFolderButton.text.append("Converting " + fileName + " to " + newFileName + newLine);
						renameFile(newLocWithSlash + fileName, tempName);
					}else{
						OpenFolderButton.text.append(newFileName + " already exists, changing name.\n");
						int i = 0;
						tempName = new File(newLocWithSlash + i+ newFileName);
						while(tempName.exists()){
							i++;
							tempName = new File(nL.toString() + i + newFileName);
						}
						OpenFolderButton.text.append(i + newFileName + " is the new name\n");
						OpenFolderButton.text.append("Copying " + fileName + newLine);
						
						p = Runtime.getRuntime().exec(cmds);
						p.waitFor();
						OpenFolderButton.text.append("Converting " + fileName + " to " + newFileName + newLine);
						renameFile(newLocWithSlash + fileName, tempName);
					}
					counter++;
				}
			}
		}
		
	}
	

	private void renameFile(String oldName, File newName) {
		File old = new File(oldName);
		old.renameTo(newName);
	}

	//maybe reduce this into just one method, that the word make it backwards.
	//same for this run from copyfiles
	private String nameOfFile(String filename) {
		ArrayList<String> nameOftheTheFile = new ArrayList<String>();
		for(int i =filename.length() - 1; i >= 0; i--){
			char character = filename.charAt(i);
			if(character != '/'){
				nameOftheTheFile.add("" + character);
			}else{
				break;
			}
		}
		
		return file(nameOftheTheFile);
	}

	private String file(ArrayList<String> r) {
		String newName = "";
		for(int i = r.size()-1; i >= 0; i--){
			newName += r.get(i);
		}
		return newName;
	}


}
