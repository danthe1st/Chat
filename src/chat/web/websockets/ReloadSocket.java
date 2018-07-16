package chat.web.websockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/reload")
public class ReloadSocket {

	private static List<Session> sessions=new ArrayList<>();
	
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
	
	public static void reloadAll(String user) {
		for (Session session : sessions) {
			try {
				if( session.isOpen()) {
					session.getBasicRemote().sendText("Reload");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
   

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    	//System.out.println("Open");
    	if (!sessions.contains(session)) {
			sessions.add(session);
		}
    	
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
    }
    
}
