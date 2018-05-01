package JavaFXGUI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.*;

/**
 * An utility class to generate salted hashes and write to the settings file. 
 * @author Kevin
 *
 */
public class SettingConfig {
	/**
	 * Generates a salted MD5 hash. 
	 * @param password The submitted password
	 * @param salt The salt
	 * @return The hash of the two put together.
	 */
	public static String generateHash(String password, String salt){
		String p = password + salt;
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
        md.update(p.getBytes());
        byte byteData[] = md.digest();
 

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        p = sb.toString();
		return p;
		
	}
	/**
	 * Writes to the settings file a new password, and generates a random hash.
	 * @param path The path of the settings.config file.
	 * @param password The password to be saved. 
	 */
	public static void writeSettingsFile(String path, String password){
		SecureRandom random = new SecureRandom();
		String salt = new BigInteger(130, random).toString(32);
		String passwordHash = generateHash(password, salt);
		
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(path, false));
			writer.println(passwordHash);
			writer.println(salt);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
