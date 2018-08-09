package chat.web.websockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/reload")
public class ReloadSocket {

	private static List<Session> sessions=new ArrayList<>();
	private static Map<String, List<Session>> sessionsInChat=new HashMap<String, List<Session>>();
	public static void reloadAll() {
		for (Session session : sessions) {
			try {
				if(session.isOpen()) {
					session.getBasicRemote().sendText("Reload");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void reload(String chat) {
		sessionsInChat.forEach((k,v)->{
			if (k.equals(chat)) {
				for (Session session : v) {
					try {
						session.getBasicRemote().sendText("Reload");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    	//System.out.println("Open");
    	if (!sessions.contains(session)) {
			sessions.add(session);
		}
    }
    @OnMessage
    public void onMessage(String message,Session session) {
    	if (!sessionsInChat.containsKey(message)) {
    		sessionsInChat.put(message, new ArrayList<>());
		}
    	sessionsInChat.get(message).add(session);
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
    	//System.out.println("Error");
    }
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    	//System.out.println("End "+closeReason.getReasonPhrase());
    	if (sessions.contains(session)) {
			sessions.remove(session);
		}
    	sessionsInChat.forEach((k,v)->{
    		if (v.contains(session)) {
				v.remove(session);
				if (v.isEmpty()) {
					sessionsInChat.remove(k);
				}
			}
    	});
    }
}
