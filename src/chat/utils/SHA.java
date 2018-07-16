package chat.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {

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
}
