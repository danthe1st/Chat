package chat.utils;

import chat.ChatData;
/**
 * automatically saves Chatdata on Program end
 * @author Daniel Schmid
 */
public class AutoSaver implements Runnable {

	private ChatData toSave;
	public AutoSaver(ChatData toSave) {
		this.toSave=toSave;
		Runtime.getRuntime().addShutdownHook(new Thread(this));
	}
	/**
	 * saves Data
	 */
	@Override
	public void run() {
		toSave.saveUsers();
		toSave.saveChats();
	}
}
