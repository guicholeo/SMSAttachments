import javax.swing.*;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.text.DefaultCaret;
/**
* CopyAttachemnts.
* @author Luis Sanchez @leosanchez16 
* Little bug when the user click cancel and tries to open the folder again, it freezes. fix?
*/
	
public class OpenFolderButton implements ActionListener{
	private String username = System.getProperty("user.home");
	private int returnVal;
	private JFileChooser openWindow;
	JFrame frame;
	static JTextArea text;
	static JScrollPane scrollPane;
	static SwingWorker worker;

	public void runGUI(){
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(500,250));
		frame.setLocationRelativeTo(null);
		frame.setTitle("Copy Attachments");
		frame.setResizable(false);
		JButton button1 = new JButton();
		text = new JTextArea(10,40);
		DefaultCaret caret = (DefaultCaret)text.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane = new JScrollPane(text);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		button1.setText("Open folder");
		frame.setLayout(new FlowLayout());
		frame.add(button1);
		frame.add(scrollPane);
		OpenFolderButton t = new OpenFolderButton();
		button1.addActionListener(t);
		frame.setVisible(true);
		text.append("Welcome to CopyAttachments. This program will copy all your SMS attachments to a folder in "
				+ "your Desktop. Only for Mac right now. Please select the backup folder and let the program do the rest.\n");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void actionPerformed(ActionEvent e) {
		//fix this when clicking cancel.
		if (worker!=null){
			worker.cancel(true);
        }
		worker = new SwingWorker(){
            @Override
            protected Integer doInBackground() {
              //  try
                //{
                	openWindow = new JFileChooser(username + "/Library/Application Support/MobileSync/Backup");
                	openWindow.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            		returnVal = openWindow.showOpenDialog(frame);
            		File path = openWindow.getSelectedFile();
            		if(returnVal == JFileChooser.APPROVE_OPTION && checkIfValidFolder(path)) {
            			 try {
            				 //what if the folder doesnt contain the file madifest? or what ever is called? fix.
            				CopyAttachments doWork = new CopyAttachments(path);
            			} catch (Exception e1) {
            				e1.printStackTrace();
            			}
            		}else{
            			JOptionPane.showMessageDialog(null, "Thank you for using the program, please close the program to select a folder again.");
            		}
               // }catch(Exception ex){} //need?
                return 0;
            }       
        };
        worker.execute();//Schedules this SwingWorker for execution on a worker thread.
		
		
	}

	protected boolean checkIfValidFolder(File path) {
		if(!path.toString().contains("Library/Application Support/MobileSync/Backup")){
			boolean validLocation = false;
			while (!validLocation){
				JOptionPane.showMessageDialog(null, "Please Select the appropriate folder");
				returnVal = openWindow.showOpenDialog(frame);
				path = openWindow.getSelectedFile();
				if(returnVal == JFileChooser.APPROVE_OPTION &&
						path.toString().contains("Library/Application Support/MobileSync/Backup"))
					validLocation = true;
				if(returnVal == JFileChooser.CANCEL_OPTION){
					return false;
				}
			}
			return true;
		}
		return true;
	}	
}