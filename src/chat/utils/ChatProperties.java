package chat.utils;

public class ChatProperties {
	//default-Properties
	private static final int MAX_CHARS_ALLOWED_IN_CHAT=46;
	private static final int MAX_CHARS_ALLOWED_IN_CHATROOM=46;
	private static final int MAX_USERS=46;
	private static final int REFRESH_TIME=10;
	private static final int MAX_FILES_ALLOWED=50;
	private static final int MAX_CHATS_CACHED=5;
	private static final int MAX_NUM_CHATS=5;
	private static final boolean FILES_ALLOWED=false;
	
	public static int getMaxChatCharactarsAllowedIn() {
		return getIntProperty("chat.maxCharsAllowedIn", MAX_CHARS_ALLOWED_IN_CHAT);
	}
	public static int getMaxChatroomCharactarsAllowedIn() {
		return getIntProperty("chat.maxCharsAllowedInRoom", MAX_CHARS_ALLOWED_IN_CHATROOM);
	}
	public static int getMaxUsers() {
		return getIntProperty("chat.maxUsers", MAX_USERS);
	}
	public static int getRefreshTime() {
		return getIntProperty("chat.refreshTime", REFRESH_TIME);
	}
	public static boolean isFilesAllowed() {
		return getBooleanProperty("chat.filesAllowed", FILES_ALLOWED);
	}
	public static int getMaxFilesAllowed() {
		return getIntProperty("chat.maxFilesAllowed", MAX_FILES_ALLOWED);
	}
	public static int getMaxChatsCached() {
		return getIntProperty("chat.maxChatsCached", MAX_CHATS_CACHED);
	}
	public static int getMaxNumChats() {
		return getIntProperty("chat.maxNumChats", MAX_NUM_CHATS);
	}
	private static int getIntProperty(String name,int defaultValue) {
		final String property=System.getProperty(name);
		
		if(property==null) {
			System.setProperty(name, String.valueOf(defaultValue));
			return defaultValue;
			
		}
		try {
			if(Integer.parseInt(property)>=0) {
				return Integer.parseInt(property);
			}
			return defaultValue;
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	private static boolean getBooleanProperty(String name,boolean defaultValue) {
		final String property=System.getProperty(name);
		
		if(property==null) {
			System.setProperty(name, String.valueOf(defaultValue));
			return defaultValue;
			
		}
		try {
			if(Integer.parseInt(property)>=0) {
				return Boolean.parseBoolean(property);
			}
			return defaultValue;
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
}
