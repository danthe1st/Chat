package chat;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoom implements Serializable{
	private static final long serialVersionUID = 7495864186215300410L;
	private final long timeCreated;
	private ArrayList<ChatMsg> msgs;
	public ChatRoom() {
		timeCreated=System.currentTimeMillis();
		msgs=new ArrayList<>();
	}
	public long getTimeCreated() {
		return timeCreated;
	}
	/**
	 * adds a Message in this Chatroom
	 * @param msg
	 */
	public void writeMsg(String msg) {
		msgs.add(new ChatMsg(msg,this));
	}
	int getId(ChatMsg msg) {
		return msgs.indexOf(msg);
	}
	public ChatMsg[] getMsgs() {
		ChatMsg[] msgs=new ChatMsg[this.msgs.size()];
		for (int i = 0; i < this.msgs.size(); i++) {
			msgs[this.msgs.size() - i - 1]=this.msgs.get(i);
		}
		return msgs;
	}
	/**
	 * deletes a Message
	 * @param messageId the id of the Message
	 * @return <code>true</code> if it worked
	 */
	public boolean delMsg(int messageId) {
		return msgs.remove(messageId)!=null;
	}
}
