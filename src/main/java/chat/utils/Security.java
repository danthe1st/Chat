package chat.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;

/**
 * Class for Security Staff
 * @author Daniel Schmid
 *
 */
public class Security {
	private static final String ATTRIB_KEYPAIR="keyPair";
	/**
	 * hashes a String using SHA-256
	 * @param toHash the String to hash
	 * @return the hash of the String
	 */
	public static String hash(String toHash) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(
					toHash.getBytes(StandardCharsets.UTF_8));
			Encoder encoder=Base64.getEncoder();
			//BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encodeToString(encodedhash).replaceAll("\r", "").replaceAll("\n", "");
		} catch (NoSuchAlgorithmException e) {}
		return null;
	}
	/**
	 * gets the {@link KeyPair} of a {@link HttpSession}
	 * @param session the session from the KeyPair
	 * @return the {@link KeyPair} stored in the {@link HttpSession} or null if there is no {@link KeyPair} stored
	 */
	private static KeyPair getKeyPair(HttpSession session){
		KeyPair keyPairs=null;
		if (session.getAttribute(ATTRIB_KEYPAIR)==null) {
			return null;
		}
		else {
			keyPairs=(KeyPair) session.getAttribute(ATTRIB_KEYPAIR);
		}
		return keyPairs;
	}
	/**
	 * generates a RSA {@link KeyPair}
	 * @param session the {@link HttpSession}
	 * @return a generated {@link KeyPair}
	 */
	public static String getRSAPublicKey(HttpSession session) {
		KeyPair keyPairs=null;
		keyPairs=getKeyPair(session);
		Key publicKey;
		if (keyPairs==null) {
			KeyPair kp=null;
		    try {
		    	KeyPairGenerator kpg;
		        kpg = KeyPairGenerator.getInstance("RSA");
		        kpg.initialize(4096);
		        kp = kpg.genKeyPair();
		    } catch(NoSuchAlgorithmException e) {

		    }
			if (kp==null) {
				return null;
			}
			session.setAttribute(ATTRIB_KEYPAIR, kp);
		    publicKey = kp.getPublic();
		}
		else {
			publicKey=getKeyPair(session).getPublic();
		}
		Encoder encoder=Base64.getEncoder();
		//BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encodeToString(publicKey.getEncoded()).replaceAll("\r", "").replaceAll("\n", "");
	}
	/**
	 * decryptes with with the {@link KeyPair} stored in the {@link HttpSession}, using RSA
	 * @param encrypted the encrypted {@link String}
	 * @param session the {@link HttpSession}
	 * @return the decrypted {@link String}
	 */
	public static String decryptRSA(String encrypted,HttpSession session) {
		KeyPair kp=getKeyPair(session);
		if (kp==null) {
			return null;
		}
		PrivateKey priKey=kp.getPrivate();
		try {
			Cipher cipher = Cipher.getInstance("RSA");  
	        cipher.init(Cipher.DECRYPT_MODE, priKey);//vllt Cipher.DECRYPT_MODE
	        Decoder decoder=Base64.getDecoder();
	        //BASE64Decoder decoder=new BASE64Decoder();
	        return new String(cipher.doFinal(decoder.decode(encrypted)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			return null;
		}
	}
}
