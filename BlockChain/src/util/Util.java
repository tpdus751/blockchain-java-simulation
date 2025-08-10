package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	
	public static String getHash(String input) {
		StringBuffer result = new StringBuffer();
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(input.getBytes());
			byte bytes[] = md.digest();
			for (int i = 0; i < bytes.length; i++) {
				result.append(
						Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1)
				);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result.toString(); 
	}
	
}
