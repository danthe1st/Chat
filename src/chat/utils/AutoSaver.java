package chat.utils;


import chat.ChatData;

public class AutoSaver implements Runnable {

	private ChatData toSave;
	public AutoSaver(ChatData toSave) {
		this.toSave=toSave;
		Runtime.getRuntime().addShutdownHook(new Thread(this));
	}
	@Override
	public void run() {
		toSave.saveUsers();
		toSave.saveChats();
	}
}
