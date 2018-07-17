package chat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import chat.utils.ChatProperties;
@XmlRootElement(name="user")
public class UserData{
	@XmlElement(name="password")
	private String password;
	@XmlElement(name="admin")
	private boolean admin;
	//@XmlElement(name="ip")
	private String ip;
	@XmlElement(name="lastMsg")
	private long lastMsg=0;
	@XmlElement(name="numFiles")
	private int numFiles=0;
	
	public UserData(String pw, String ip) {
		this.password=pw;
		this.ip=ip;
		admin=false;
	}
	public UserData() {
		this("", "");
	}
	public boolean isPasswordCorrect(String pwHash) {
		return pwHash.equals(this.password);
	}
	
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin() {
		this.admin = true;
	}
	public void unsetAdmin() {
		this.admin = false;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public boolean renewLastMsg() {
		if (System.currentTimeMillis()-500<lastMsg) {
			lastMsg=System.currentTimeMillis();
			return false;
		}
		lastMsg=System.currentTimeMillis();
		
		return true;
	}
	public boolean isNumFilesOK() {
		return numFiles<=ChatProperties.getMaxFilesAllowed();
	}
	public void addFile() {
		numFiles++;
	}
	
	
	
}