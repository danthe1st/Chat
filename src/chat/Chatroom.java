package chat;

import java.io.Serializable;
import java.util.ArrayList;

public class Chatroom implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7495864186215300410L;
	private final long timeCreated;
	private ArrayList<ChatMsg> msgsNew;
	
	public Chatroom() {
		timeCreated=System.currentTimeMillis();
		msgsNew=new ArrayList<>();
	}

	public long getTimeCreated() {
		return timeCreated;
	}
	
	public void writeMsg(String msg) {
		msgsNew.add(new ChatMsg(msg,this));
	}
	int getId(ChatMsg msg) {
		return msgsNew.indexOf(msg);
	}
	public ChatMsg[] getMsgs() {
		
		ChatMsg[] msgs=new ChatMsg[msgsNew.size()];
		for (int i = 0; i < msgsNew.size(); i++) {
			msgs[msgsNew.size() - i - 1]=msgsNew.get(i);
		}
		return msgs;
	}
	public boolean delMsg(int created) {
		return msgsNew.remove(created)!=null;
	}
	
}
