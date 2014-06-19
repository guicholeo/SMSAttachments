import java.io.UnsupportedEncodingException; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
 

/**
 * Source Code found here: http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
 * 
© 2010 AnyExample.com 

We've wrote everything here and own copyright
Permission is hereby granted, free of charge, 
to any person obtaining a copy of AnyExample.com articles, 
examples and source code fragments,
to deal in included source code(the "Source Code") without without restriction, 
including without limitation the rights to use, copy, modify, merge, distribute, 
sublicense, and/or sell copies of the Source Code, 
and to permit persons to whom the Source Code is furnished to do so.
 */
public class Hashing { 
 
    private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
 
    public static String SHA1(String text) 
	    throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
	    MessageDigest md;
	    md = MessageDigest.getInstance("SHA-1");
	    byte[] sha1hash = new byte[40];
	    md.update(text.getBytes("iso-8859-1"), 0, text.length());
	    sha1hash = md.digest();
	    return convertToHex(sha1hash);
    } 
} 