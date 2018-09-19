package chat;

import java.io.Serializable;
/**
 * a Chat Message
 * @author Daniel Schmid
 */
public class ChatMsg implements Serializable{
	private static final long serialVersionUID = 5812756441146183447L;
	private String msg;
	private ChatRoom room;
	
	public ChatMsg(String msg, ChatRoom room) {
		this.msg=msg;
		this.room=room;
	}
	public String getMsg() {
		return msg.replaceAll("�", "");
	}
	public int getId() {
		return room.getId(this);
	}
	/**
	 * gets the Link of the Message(if the Message has a link
	 * @return the Link or <code>null</code>
	 */
	public String getLink() {
		String[] splitted=msg.split("�");
		if (splitted.length<2) {
			return null;
		}
		
		return splitted[1];
	}
	/**
	 * tests if a link is an Image
	 * @param link the link
	 * @return <code>true</code> if it ends with an Image file-ending
	 */
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
