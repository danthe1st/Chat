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
	private void resetActiveChats() {
		AutoDelMap<String, Chatroom> activeChats=new AutoDelMap<>(ChatProperties.getMaxChatsCached());
		activeChats.setOnRemove((k,v)->{saveChat(k, v);});
		this.activeChats=activeChats;
	}
	
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
	
	
	private UserData getUser(String uname) {
		
		if (usersLoggedIn.containsKey(uname)) {
			return usersLoggedIn.get(uname);
		}
		UserData user=loadUser(uname);
		return user;
	}
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
	private UserData loadUser(String user) {
		File userDir=new File(pathname+"/"+usersDir);
		if (!userDir.exists()) {
			return null;
		}
		//return (UserData) load(usersDir+"/"+user+userSuffix);
		return XMLController.loadUser(new File(pathname+"/"+usersDir+"/"+user+userSuffix));
	}
	private void saveUser(String username,UserData user) {
		File userDir=new File(pathname+"/"+usersDir);
		if (!userDir.exists()) {
			userDir.mkdirs();
		}
//		save(usersDir+"/"+username+userSuffix,user);
		XMLController.saveUser(user, new File(pathname+"/"+usersDir+"/"+username+".xml"));
	}
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
	public boolean isLoggedIn(String user) {
		if (!hasUser(user)) {
			return false;
		}
		return usersLoggedIn.containsKey(user);
	}
	public void login(String user) {
		
		//getUser(user).setLoggedIn(true);
		UserData userdata=getUser(user);
		if (user==null) {
			return;
		}
		usersLoggedIn.put(user, userdata);
		System.out.println("User logged in: "+user);
		//save();
	}
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
	
	public Set<String> getAllUsernames(){
		
		Set<String> allUsers=new HashSet<>();
		for (String string : activeChats.keySet()) {
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
	public Set<String> getUsersLoggedIn(){
		Set<String> users=new HashSet<String>();
		users.clear();
		
		usersLoggedIn.forEach((name,data)->{
			users.add(name);
		});
		
		return users;
	}
	public boolean isPasswordCorrect(String username, String pwHash) {
		if (!hasUser(username)) {
			return false;
		}
		return getUser(username).isPasswordCorrect(pwHash);
	}
	public boolean isAdmin(String username) {
		if (!hasUser(username)) {
			return false;
		}
		UserData user=getUser(username);
		return user.isAdmin();
	}
	public void setAdmin(String user) {
		if (!hasUser(user)) {
			return;
		}
		if (isAdmin(user)) {
			return;
		}
		getUser(user).setAdmin();
		System.out.println(user+" is now Admin");
		//saveUsers();
	}
	public void unsetAdmin(String user) {
		if (!hasUser(user)) {
			return;
		}
		if (!isAdmin(user)) {
			return;
		}
		getUser(user).unsetAdmin();
		System.out.println(user+" is not Admin any more");
		//saveUsers();
	}
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
	public void unban(String ip) {
		for (String string : bannedIPs) {
			if (string.equals(ip)) {
				bannedIPs.remove(string);
				System.out.println("unbanned IP: "+ip);
			}
		}
		saveBannedIPs();
	}
	public boolean isBanned(String ip) {
		for (String string : bannedIPs) {
			if (string.equals(ip)) {
				return true;
			}
		}
		return false;
	}
	public List<String> getBans(){
		return bannedIPs;
	}
	
	public String getIp(String user) {
		if (!hasUser(user)) {
			if (hasIpFormat(user)) {
				return user;
			}
			return null;
		}
		return getUser(user).getIp();
	}
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
	public boolean renew(String user) {
		return getUser(user).renewLastMsg();
	}
	public void addFile(String user) {
		getUser(user).addFile();
	}
	public boolean isNumFilesOK(String user) {
		return getUser(user).isNumFilesOK();
	}
	
	public Chatroom getChat(String name) {
		if (activeChats.containsKey(name)) {
			return activeChats.get(name);
		}
		return loadChat(name);
	}
	
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
