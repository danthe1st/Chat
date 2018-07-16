package chat.web.listeners;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import chat.ChatData;
import chat.utils.Constants;

public class SessionListener implements HttpSessionListener{

	@Override
	public void sessionCreated(HttpSessionEvent se) {}

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
