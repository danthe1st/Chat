package chat.web.listeners;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import chat.ChatData;
import chat.utils.Constants;
/**
 * a listenet when a Session is created or destroyed
 * @author Daniel Schmid
 *
 */
@WebListener
public class SessionListener implements HttpSessionListener{

	/**
	 * when a session is created -> do nothing
	 */
	@Override
	public void sessionCreated(HttpSessionEvent se) {}
	
	/**
	 * Listener when a session is destroyes<br>
	 * loggs out the user, who used is logged in in the session
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		String user=(String) se.getSession().getAttribute(Constants.UNAME_FIELD);
		ChatData chatData=ChatData.getChatData(se.getSession().getServletContext());
		if (chatData.isLoggedIn(user)) {
			chatData.logout(user);
			System.out.println("Logged out because of Session invalidation");
		}
	}

}
