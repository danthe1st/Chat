package chat;

import java.io.Serializable;

public class ChatMsg implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5812756441146183447L;
	private String msg;
	private Chatroom room;
	
	public ChatMsg(String msg, Chatroom room) {
		this.msg=msg;
		this.room=room;
	}
	
	public String getMsg() {
		return msg.replaceAll("°", "");
	}
	
	public int getId() {
		return room.getId(this);
	}
	public String getLink() {
		String[] splitted=msg.split("°");
		if (splitted.length<2) {
			return null;
		}
		
		return splitted[1];
	}
	public static boolean isImage(String link) {
		if (link==null) {
			return false;
		}
		if(link.endsWith("jpg")) {
			return true;
		}
		if(link.endsWith("png")) {
			return true;
		}
		if(link.endsWith("gif")) {
			return true;
		}
		return false;
	}
}
