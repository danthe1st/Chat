package chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import chat.utils.AutoDelMap;
import chat.utils.AutoSaver;
import chat.utils.ChatProperties;
import chat.utils.Constants;
import chat.utils.XMLController;
/**
 * the core Class of the Chat
 * @author Daniel Schmid
 */
public class ChatData{
	
	private Map<String, UserData> usersLoggedIn = new HashMap<String, UserData>();
	private Map<String, Chatroom> activeChats;
	private List<String> bannedIPs=new ArrayList<>();
	private static final String chatsDir="chats/";
	private static final String usersDir="users/";
	private static final String chatsSuffix=".chat";
	private static final String userSuffix=".xml";
	public static final String pathname="chat/Settings";
	private ChatData(ServletContext context) {
		resetActiveChats();
		loadBannedIPs();
		context.setAttribute(Constants.attributeChatData, this);
		new AutoSaver(this);
	}
	/**
	 * resets/initializes all the Chats
	 */
	private void resetActiveChats() {
		AutoDelMap<String, Chatroom> activeChats=new AutoDelMap<>(ChatProperties.getMaxChatsCached());
		activeChats.setOnRemove((k,v)->{saveChat(k, v);});
		this.activeChats=activeChats;
	}
	/**
	 * gets the instance of the {@link ChatData} (Singelton)
	 * @param context the  {@link ServletContext}
	 * @return the (main) instance of the {@link ChatData}
	 */
	public static ChatData getChatData(ServletContext context) {
		ChatData data=(ChatData) context.getAttribute(Constants.attributeChatData);
		if (data==null) {
			data=new ChatData(context);
		}
		File file=new File(pathname+"/"+chatsDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return data;
	}
	/**
	 * gets the {@link UserData} of a user
	 * @param uname the username
	 * @return the Data of the user
	 */
	private UserData getUser(String uname) {
		
		if (usersLoggedIn.containsKey(uname)) {
			return usersLoggedIn.get(uname);
		}
		UserData user=loadUser(uname);
		return user;
	}
	/**
	 * checks if there exists a user with the specified name
	 * @param user the username
	 * @return true if the user exists, else false
	 */
	public boolean hasUser(String user) {
		if (usersLoggedIn.containsKey(user)) {
			return true;
		}
		File userDir=new File(pathname+"/"+usersDir);
		if (!userDir.exists()) {
			return false;
		}
		File userFile=new File(pathname+"/"+usersDir+"/"+user+userSuffix);
		return userFile.exists();
	}
	/**
	 * loads a user
	 * @param user the username
	 * @return the loaded {@link UserData} or null if the user doesn't exist
	 */
	private UserData loadUser(String user) {
		File userDir=new File(pathname+"/"+usersDir);
		if (!userDir.exists()) {
			return null;
		}
		//return (UserData) load(usersDir+"/"+user+userSuffix);
		return XMLController.loadUser(new File(pathname+"/"+usersDir+"/"+user+userSuffix));
	}
	/**
	 * saves a user
	 * @param username the username of the user
	 * @param user the {@link UserData}
	 */
	private void saveUser(String username,UserData user) {
		File userDir=new File(pathname+"/"+usersDir);
		if (!userDir.exists()) {
			userDir.mkdirs();
		}
//		save(usersDir+"/"+username+userSuffix,user);
		XMLController.saveUser(user, new File(pathname+"/"+usersDir+"/"+username+".xml"));
	}
	/**
	 * registers an User<br>
	 * only works if passwordHash equals the passwordConfHash
	 * @param username the username
	 * @param passwordHash the hashed Password of the user
	 * @param passwordConfHash the hashed password-confirm field
	 * @param ip the IP-Address of the User
	 * @return true if it worked
	 */
	public boolean registerUser(String username, String passwordHash,String passwordConfHash,String ip) {
		if(!passwordHash.equals(passwordConfHash)) {
			return false;
		}
		if (!hasUser(username)) {
			UserData user=new UserData(passwordHash,ip);
			usersLoggedIn.put(username, user);
			System.out.println("User registered: "+username+" ["+ip+"]");
			saveUser(username, user);
			return true;
		}
		return false;
	}
	/**
	 * deletes an user
	 * @param username the username of the user to delete
	 */
	public void unregisterUser(String username) {
		if (hasUser(username)) {
			usersLoggedIn.remove(username);
			File userDir=new File(pathname+"/"+usersDir);
			if (userDir.exists()) {
				File userFile=new File(pathname+"/"+usersDir+"/"+username+userSuffix);
				if (userFile.exists()) {
					userFile.delete();
				}
			}
			System.out.println("User unregistered: "+username);
		}
		//saveUsers();
	}
	/**
	 * checks if a user is logged in or not
	 * @param user the username
	 * @return true if the user is logged in, else false
	 */
	public boolean isLoggedIn(String user) {
		if (!hasUser(user)) {
			return false;
		}
		return usersLoggedIn.containsKey(user);
	}
	/**
	 * sets a user to be logged in
	 * @param user the user
	 */
	public void login(String user) {
		UserData userdata=getUser(user);
		if (user==null) {
			return;
		}
		usersLoggedIn.put(user, userdata);
		System.out.println("User logged in: "+user);
	}
	/**
	 * logges out a user
	 * @param user the username
	 */
	public void logout(String user) {
		if (!hasUser(user)) {
			return;
		}
		saveUser(user, usersLoggedIn.get(user));
		usersLoggedIn.remove(user);
		
		//getUser(user).setLoggedIn(false);
		System.out.println("User logged out: "+user);
		if (getUsersLoggedIn().isEmpty()) {
			clearData();
		}
	}
	/**
	 * gets all registered users
	 * @return a {@link Set} of all registered usernames
	 */
	public Set<String> getAllUsernames(){
		Set<String> allUsers=new HashSet<>();
		for (String string : usersLoggedIn.keySet()) {
			allUsers.add(string); 
		}
		for (String filename : new File(pathname+"/"+usersDir).list()) {
			if (filename.endsWith(userSuffix)) {
				String chatname=filename.substring(0, filename.lastIndexOf(userSuffix));
				allUsers.add(chatname);
			}
		}
		return allUsers;
	}
	/**
	 * gets all logged in users
	 * @return a {@link Set} of all usernames logged in
	 */
	public Set<String> getUsersLoggedIn(){
		Set<String> users=new HashSet<String>();
		users.clear();
		usersLoggedIn.forEach((name,data)->{
			users.add(name);
		});
		return users;
	}
	/**
	 * check if a password is correct
	 * @param username the username
	 * @param pwHash the Hash of the password
	 * @return true if the password is correct
	 */
	public boolean isPasswordCorrect(String username, String pwHash) {
		if (!hasUser(username)) {
			return false;
		}
		return getUser(username).isPasswordCorrect(pwHash);
	}
	/**
	 * checks if a user is an Admin
	 * @param username the username of the user
	 * @return true if the user is an Admin
	 */
	public boolean isAdmin(String username) {
		if (!hasUser(username)) {
			return false;
		}
		if (!ChatProperties.isAdminAllowed()) {
			return false;
		}
		UserData user=getUser(username);
		return user.isAdmin();
	}
	/**
	 * sets a user to be an Admin
	 * @param user the username of the user to be admin
	 */
	public void setAdmin(String user) {
		if (!hasUser(user)) {
			return;
		}
		if (isAdmin(user)) {
			return;
		}
		getUser(user).setAdmin();
		System.out.println(user+" is now Admin");
	}
	/**
	 * removes Admin Permissions from an User
	 * @param user the username of the user to be admin not any longer
	 */
	public void unsetAdmin(String user) {
		if (!hasUser(user)) {
			return;
		}
		if (!isAdmin(user)) {
			return;
		}
		getUser(user).unsetAdmin();
		System.out.println(user+" is not Admin any more");
	}
	/**
	 * bans an IP from using the Chat
	 * @param ip the IP-Adress to be banned
	 */
	public void ban(String ip) {
		if (ip==null) {
			return;
		}
		if (!isBanned(ip)) {
			bannedIPs.add(ip);
			System.out.println("banned IP: "+ip);
		}
		saveBannedIPs();
	}
	/**
	 * unbans an IP from using the Chat
	 * @param ip the IP-Adress to be unbanned
	 */
	public void unban(String ip) {
		for (String string : bannedIPs) {
			if (string.equals(ip)) {
				bannedIPs.remove(string);
				System.out.println("unbanned IP: "+ip);
			}
		}
		saveBannedIPs();
	}
	/**
	 * checks if an IP-Adress is banned
	 * @param ip the IP-Adress
	 * @return true if the ip is banned, else false
	 */
	public boolean isBanned(String ip) {
		for (String string : bannedIPs) {
			if (string.equals(ip)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * get all banned IPs
	 * @return a {@link List} of all banned IPs
	 */
	public List<String> getBans(){
		return bannedIPs;
	}
	/**
	 * gets the IP of a user
	 * @param user the username of the user
	 * @return the ip of the user, if no user found and the specified user has the ip-format it will be returned, else <code>null</code> will be returned
	 */
	public String getIp(String user) {
		if (!hasUser(user)) {
			if (hasIpFormat(user)) {
				return user;
			}
			return null;
		}
		return getUser(user).getIp();
	}
	/**
	 * checks if a {@link String} has the format of an IP-Adress
	 * @param ip the String to be tested
	 * @return true if the specified {@link String} is an IP-Adress
	 */
	private boolean hasIpFormat(String ip) {
		String[] parts=ip.split("\\.");
		if (parts.length!=4) {
			return false;
		}
		for (String string : parts) {
			try {
				if (Integer.parseInt(string)>255||Integer.parseInt(string)<0) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	/**
	 * renews the time of the last Message<br>
	 * 
	 * @param user
	 * @return true if the last Message was sent greater than {@link ChatProperties#getRefreshTime()}, else false
	 */
	public boolean renew(String user) {
		return getUser(user).renewLastMsg();
	}
	/**
	 * adds a File to the user(increments the File Counter)
	 * @param user the username of the user
	 */
	public void addFile(String user) {
		getUser(user).addFile();
	}
	/**
	 * checks if a specified user is allowed to send a File
	 * @param user the name of the user
	 * @return true if the user is allowed to send a File
	 */
	public boolean isNumFilesOK(String user) {
		return getUser(user).isNumFilesOK();
	}
	/**
	 * gets a Chat
	 * @param name the name of the Chat
	 * @return the Chat represented as {@link ChatData}
	 */
	public Chatroom getChat(String name) {
		if (activeChats.containsKey(name)) {
			return activeChats.get(name);
		}
		return loadChat(name);
	}
	/**
	 * creates a Chat<br>
	 * if there is already a Chat this Chat will be used
	 * @param name the name of the Chat
	 * @return the created Chat
	 */
	public Chatroom createChat(String name) {
		Chatroom chat=getChat(name);
		if(chat!=null) {
			return chat;
		}
		chat=new Chatroom();
		activeChats.put(name, chat);
		saveChat(name, chat);
		System.out.println("Chat created: "+name);
		//saveChats();
		return chat;
	}
	/**
	 * checks if a user is allowed create more Chats
	 * @return true if more Chats can be created
	 */
	public boolean isNumOfChatsOK() {
		
		File chatsDir=new File(pathname+"/"+ChatData.chatsDir);
		int num=0;
		for (String dir : chatsDir.list()) {
			if (dir.endsWith(chatsSuffix)) {
				num++;
			}
		}
		return num<ChatProperties.getMaxNumChats();
	}
	/**
	 * deletes a Chat
	 * @param name the name of the Chat to be deleted
	 */
	public void deleteChat(String name) {
		boolean deleted=false;
		if (activeChats.containsKey(name)) {
			activeChats.remove(name);
			deleted=true;
		}
		File file=new File(pathname+"/"+chatsDir+name+chatsSuffix);
		if (file.exists()) {
			file.delete();
			deleted=true;
		}
		if (deleted) {
			System.out.println("Chat deleted: "+name);
		}
	}
	/**
	 * checks if there is a Chat with a specified name
	 * @param name the name of the Chat to be checked
	 * @return true if the Chat exists
	 */
	public boolean hasChat(String name) {
		return getChat(name)!=null;
	}
	public Set<String> listChats() {
		Set<String> chats=new HashSet<>();
		for (String string : activeChats.keySet()) {
			chats.add(string);
		}
		for (String filename : new File(pathname+"/"+chatsDir).list()) {
			if (filename.endsWith(chatsSuffix)) {
				String chatname=filename.substring(0, filename.lastIndexOf(chatsSuffix));
				chats.add(chatname);
			}
		}
		return chats;
	}
	
	
	public static boolean endsWithSpaces(String text) {
		return text.endsWith(" ");
	}
	
	private void clearData() {
		activeChats.clear();
	}
	
	@SuppressWarnings("unchecked")
	private void loadBannedIPs() {
		List<String> ips = null;
		try {
			ips = (List<String>) load("IPs.dat");
		} catch (Exception e) {
		}
		if (ips==null) {
			ips=new ArrayList<>();
		}
		bannedIPs=ips;
	}
	private void saveBannedIPs() {
		save("IPs.dat",bannedIPs);
	}
	
	
	private Chatroom loadChat(String name) {
		Chatroom chat=(Chatroom)load(chatsDir+name+chatsSuffix);
		if (chat==null) {
			return null;
		}
		activeChats.put(name, chat);
		return chat;
	}
	public void saveChat(String name,Chatroom chat) {
		File file=new File(pathname+"/"+chatsDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		save(chatsDir+name+chatsSuffix, chat);
	}
	public void saveChats() {
		activeChats.forEach((name,chat)->{
			saveChat(name, chat);
		});
	}
	
	public void saveUsers() {
		usersLoggedIn.forEach((username,userdata)->{
			saveUser(username, userdata);
		});
	}
	private void save(String filepath, Object toSave) {
		try {
			File path=new File(pathname);
			if (!path.exists()) {
				path.mkdirs();
			}
			
			File file=new File(pathname+"/"+filepath);
			if (toSave==null) {
				file.delete();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		
			FileOutputStream fos=new FileOutputStream(file);
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(toSave);
			oos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Object load(String filepath) {
		try {
			File file=new File(pathname+"/"+filepath);
			if (file.exists()) {
				FileInputStream fis=new FileInputStream(file);
				ObjectInputStream ois=new ObjectInputStream(fis);
				Object data=ois.readObject();
				ois.close();
				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	

	
}
