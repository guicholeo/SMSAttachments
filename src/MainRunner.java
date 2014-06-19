import java.io.IOException;
/**
*A simple program that will copy all the SMS attachments from your iPhone/iPod/iPad back up.
*Hashing.java is code found online, and FileLister and FileInfo are by @author Roy van Rijn and using it with permission.
* CopyAttachemnts.
* @author Luis Sanchez @leosanchez16 
*/

public class MainRunner {

	public static void main(String[] args) throws IOException{
		//based the progress bar? with the amount of items on the map
		
		OpenFolderButton o = new OpenFolderButton();
		o.runGUI();
	}
}
