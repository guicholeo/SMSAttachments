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
*/
	
public class OpenFolderButton implements ActionListener{
	private File path;
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

	@Override
	public void actionPerformed(ActionEvent e) {

		//fix this when clicking cancel.
		if (worker!=null){
			worker.cancel(true);
        }
		worker = new SwingWorker(){
            @Override
            protected Integer doInBackground() {//Perform the required GUI update here.
                try
                {
                	JFileChooser f = new JFileChooser("/Users/leo/Library/Application Support/MobileSync/Backup");
            		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            		int returnVal = f.showOpenDialog(frame);
            		path = f.getSelectedFile();
            		if(!path.toString().contains("Library/Application Support/MobileSync/Backup")){
            			boolean valid = false;
            			while (!valid){
            				JOptionPane.showMessageDialog(null, "Please Select the appropriate folder");
            				returnVal = f.showOpenDialog(frame);
            				path = f.getSelectedFile();
            				if(returnVal == JFileChooser.APPROVE_OPTION &&
            						path.toString().contains("Library/Application Support/MobileSync/Backup"))
            					valid = true;
            				else
            					valid = true;
            			}
            		}
            		 if (returnVal == JFileChooser.APPROVE_OPTION) {
            			 path = f.getSelectedFile();
            			 try {
            				
            				CopyAttachments doWork = new CopyAttachments(path);
            			} catch (Exception e1) {
            				e1.printStackTrace();
            			}
            		 }
                }catch(Exception ex){}
                return 0;
            }       
        };
        worker.execute();//Schedules this SwingWorker for execution on a worker thread.
		
		
	}

	
}
