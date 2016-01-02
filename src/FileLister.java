
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This class is used to list the files inside the iPhone backup directory.<br/> 
 * Translated from Python script:
 * http://stackoverflow.com/questions/3085153/how-to-parse-the-manifest-mbdb-file-in-an-ios-4-0-itunes-backup
 * 
 * @author Roy van Rijn
 * added a few lines of code and modified a few lines @author leo
 * 
 */
public class FileLister {

	private static final byte[] HEX_CHAR_TABLE = { (byte) '0', (byte) '1', (byte) '2',
		(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
		(byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
		(byte) 'd', (byte) 'e', (byte) 'f' };

	final Map<String, FileInfo> fileInfoList;
	final File backupPath;
	
	

	public FileLister(File backupPath) throws Exception {
		this.backupPath = backupPath;
		
		//Get information from Manifest.mbdb:
		File mbdb = new File(backupPath, "Manifest.mbdb");
		fileInfoList = processMbdbFile(mbdb);
	}

	int offset;

	public Map<String, FileInfo> processMbdbFile(File mbdb) throws Exception {
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(mbdb));
		byte[] checkFile = new byte[4];
		stream.read(checkFile, 0, 4);
		if (!Arrays.equals(checkFile, "mbdb".getBytes())) {
			throw new RuntimeException("mbdb file isn't valid!");
		}
		stream.read(); // Unknown bytes
		stream.read();
		offset = 6;
		

		Map<String, FileInfo> files = new HashMap<String, FileInfo>();
		while (stream.available() > 0) {

			FileInfo info = new FileInfo();
			info.setStartOffset(offset);
			info.setDomain(getString(stream));
			info.setFilename(getString(stream));
			info.setLinktarget(getString(stream));
			info.setDatahash(getString(stream));
			info.setUnknown1(getString(stream));
			info.setMode(getInt(stream, 2));
			info.setUnknown2(getInt(stream, 4));
			info.setUnknown3(getInt(stream, 4));
			info.setUserid(getInt(stream, 4));
			info.setGroupid(getInt(stream, 4));
			info.setMtime(getInt(stream, 4));
			info.setAtime(getInt(stream, 4));
			info.setCtime(getInt(stream, 4));
			info.setFilelen(getInt(stream, 8));
			info.setFlag(getInt(stream, 1));
			info.setNumprops(getInt(stream, 1));

			for (int prop = 0; prop < info.getNumprops(); prop++) {
				String propName = getString(stream);
				String propValue = getString(stream);
				info.getProperties().put(propName, propValue);
			}

			//added by me. info.getMode() == 33188 to only add the files to the Map, and not the folders too.
			String fileName = info.getFilename();
			if(info.getDomain().equalsIgnoreCase("MediaDomain") && fileName.contains("Library/SMS/Attachments/") 
					&& info.getMode() == 33188 && !fileName.contains("preview") && !fileName.contains(".DS_Store")){
				//tests
				//how to manually add the properties to the file.
				/*System.out.println("StartOffSet " + info.getStartOffset());
				System.out.println("Domain      " + info.getDomain());
				System.out.println("FileName    " + info.getFilename());
				System.out.println("LinkTarget  " + info.getLinktarget());
				System.out.println("Datahash    " + info.getDatahash());
				System.out.println("Unknown1    " + info.getUnknown1());
				System.out.println("Mode        " + info.getMode());
				System.out.println("Unknown2    " + info.getUnknown2());
				System.out.println("Unknown3    " + info.getUnknown3());
				System.out.println("Userid      " + info.getUserid());
				System.out.println("Groupid     " + info.getGroupid());
				System.out.println("Mtime       " + info.getMtime());
				System.out.println("Atime       " + info.getAtime());
				System.out.println("Ctime       " + info.getCtime());
				System.out.println("Filelen     " + info.getFilelen());
				System.out.println("Flag        " + info.getFlag());
				System.out.println("Numprops    " + info.getNumprops());
				System.out.println("Properties  " + info.getProperties()); // rename to original filename from properties
				for(Entry<String, String> entry : info.getProperties().entrySet()) {
					  String part1 = entry.getKey();
					  String part2 = entry.getValue();
					  System.out.println ("properties: ");
					  System.out.println ("part1: " + part1);
					  System.out.println ("part2: " + part2);
					  
				}
				System.out.println();*/
				files.put(makeHashString(info),info);
			}
				
		}
		return files;
	}
	
	
	private String makeHashString(FileInfo info) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String filePath = info.getDomain() + "-" + info.getFilename();
		
		return Hashing.SHA1(filePath);
	}

	private int getInt(final InputStream stream, int size) throws Exception {
		byte[] data = new byte[size];
		stream.read(data);
		offset += size;
		int value = 0;
		for (int i = 0; i < data.length; i++) {
			value = (value << 8) + (data[i] & 0xFF);
		}
		return value;
	}

	private String getString(final InputStream stream) throws Exception {
		int length = getInt(stream, 2);
		if (length == 0xFFFF) {
			return "";
		}
		offset += length;
		return getString(stream, length);
	}
	
	private String getString(final InputStream stream, int length) throws Exception {
		byte[] newString = new byte[length];
		stream.read(newString);
		return new String(newString);
	}
}
