package chat.utils;
/**
 * Class for getting System Properties
 * @author Daniel Schmid
 */
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
	private static final boolean ADMIN_ALLOWED=true;
	private static final boolean ADMIN_CHANGE_ALLOWED=true;
	/**
	 * how much characters are allowed for an standard user input(Chat Message, username)
	 * @return max allowed chars
	 */
	public static int getMaxChatCharactarsAllowedIn() {
		return getIntProperty("chat.maxCharsAllowedIn", MAX_CHARS_ALLOWED_IN_CHAT);
	}
	/**
	 * how much characters are allowed for a chat name
	 * @return max allowed chars
	 */
	public static int getMaxChatroomCharactarsAllowedIn() {
		return getIntProperty("chat.maxCharsAllowedInRoom", MAX_CHARS_ALLOWED_IN_CHATROOM);
	}
	/**
	 * how many users are allowed
	 * @return max number of users allowed
	 */
	public static int getMaxUsers() {
		return getIntProperty("chat.maxUsers", MAX_USERS);
	}
	/**
	 * time of auto-refresh(if no messages were sent)
	 * @return auto refresh time
	 */
	public static int getRefreshTime() {
		return getIntProperty("chat.refreshTime", REFRESH_TIME);
	}
	/**
	 * is it allowed to send Files
	 * @return true if files are allowed
	 */
	public static boolean isFilesAllowed() {
		return getBooleanProperty("chat.filesAllowed", FILES_ALLOWED);
	}
	/**
	 * is the admin-mode allowed (to use)
	 * @return true if the admin-mode is allowed
	 */
	public static boolean isAdminAllowed() {
		return getBooleanProperty("chat.adminAllowed", ADMIN_ALLOWED);
	}
	/**
	 * is it allowed to change if the current user is an Admin(if not it can be changed in the .xml file of the user)
	 * @return true if it is allowed to change
	 */
	public static boolean isAdminChangeAllowed() {
		return getBooleanProperty("chat.adminChangeAllowed", ADMIN_CHANGE_ALLOWED);
	}
	/**
	 * how many files a user is allowed to send
	 * @return max number of Files can be sent by a user
	 */
	public static int getMaxFilesAllowed() {
		return getIntProperty("chat.maxFilesAllowed", MAX_FILES_ALLOWED);
	}
	/**
	 * how many Chats should be cached (maximum)
	 * @return number of the Chats to be cached
	 */
	public static int getMaxChatsCached() {
		return getIntProperty("chat.maxChatsCached", MAX_CHATS_CACHED);
	}
	/**
	 * how many Chats are allowed
	 * @return the max number of allowed Chats
	 */
	public static int getMaxNumChats() {
		return getIntProperty("chat.maxNumChats", MAX_NUM_CHATS);
	}
	/**
	 * gets a int Property
	 * @param name the name of the Property
	 * @param defaultValue the default value of the Property
	 * @return the value of the property, or if an Error occurs the default Property
	 */
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
	/**
	 * gets a boolean Property
	 * @param name the name of the Property
	 * @param defaultValue the default value of the Property
	 * @return the value of the property, or if an Error occurs the default Property
	 */
	private static boolean getBooleanProperty(String name,boolean defaultValue) {
		final String property=System.getProperty(name);
		
		if(property==null) {
			System.setProperty(name, String.valueOf(defaultValue));
			return defaultValue;
			
		}
		try {
			return Boolean.parseBoolean(property);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
}
