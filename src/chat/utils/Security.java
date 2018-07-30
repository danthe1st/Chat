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
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class Security {

	public static String hash(String toHash) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(
					toHash.getBytes(StandardCharsets.UTF_8));
			return stripNonValidXMLCharacters(new String(encodedhash));
		} catch (NoSuchAlgorithmException e) {}
		return null;
	}
	private static String stripNonValidXMLCharacters(String in) {      
	    return in.replaceAll("[\\uD83D\\uFFFD\\uFE0F\\u203C\\u3010\\u3011\\u300A\\u166D\\u200C\\u202A\\u202C\\u2049\\u20E3\\u300B\\u300C\\u3030\\u065F\\u0099\\u0F3A\\u0F3B\\uF610\\uFFFC]", "").replaceAll("[^\\u0009\\u000a\\u000d\\u0020-\\uD7FF\\uE000-\\uFFFD]", "");
	}
	
	private static Map<String,KeyPair> keyPairs=new HashMap<>();
	public static String getRSAPublicKey(String sessionID) {
		Key publicKey;
		if (!keyPairs.containsKey(sessionID)) {
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
		    keyPairs.put(sessionID, kp);
			// receiving public key from where you store it
		    publicKey = kp.getPublic();
		}
		else {
			publicKey=keyPairs.get(sessionID).getPublic();
		}
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(publicKey.getEncoded()).replaceAll("\r", "").replaceAll("\n", "");
//	    KeyFactory fact;
//	    // initializing public key variable
//	    RSAPublicKeySpec pub = new RSAPublicKeySpec(BigInteger.ZERO, BigInteger.ZERO);
//	    try {
//	        fact = KeyFactory.getInstance("RSA");
//	        pub = fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
//	    } catch(NoSuchAlgorithmException e1) {
//	    } catch(InvalidKeySpecException e) {
//	    }
//	    
//	    
//	// now you should pass Modulus string onto your html(jsp) in such way
//	    
//	return pub.getModulus().toString(10);
	// send somehow this String to page, so javascript can use it
	}
	public static String decryptRSA(String encrypted,String sessionID) {
		PrivateKey priKey=keyPairs.get(sessionID).getPrivate();
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
