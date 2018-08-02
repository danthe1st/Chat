package chat.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSession;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Security {
	private static final String ATTRIB_KEYPAIR="keyPair";
	public static String hash(String toHash) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(
					toHash.getBytes(StandardCharsets.UTF_8));
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(encodedhash).replaceAll("\r", "").replaceAll("\n", "");
		} catch (NoSuchAlgorithmException e) {}
		return null;
	}
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
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(publicKey.getEncoded()).replaceAll("\r", "").replaceAll("\n", "");
	}
	public static String decryptRSA(String encrypted,HttpSession session) {
		KeyPair kp=getKeyPair(session);
		if (kp==null) {
			return null;
		}
		PrivateKey priKey=kp.getPrivate();
		try {
			Cipher cipher = Cipher.getInstance("RSA");  
	        cipher.init(Cipher.DECRYPT_MODE, priKey);//vllt Cipher.DECRYPT_MODE
	        BASE64Decoder decoder=new BASE64Decoder();
	        return new String(cipher.doFinal(decoder.decodeBuffer(encrypted)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
			return null;
		}
	}
}
