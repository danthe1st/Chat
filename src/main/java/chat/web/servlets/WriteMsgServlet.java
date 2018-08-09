package chat.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.ChatData;
import chat.utils.ChatProperties;
import chat.utils.Constants;
import chat.web.websockets.ReloadSocket;

/**
 * Servlet implementation class WriteMsgServlet
 */
@WebServlet("/writeMsg")
public class WriteMsgServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!assertUserPresent(req, resp)) {
			return;
		}
		String user=getUser(req);
		String name=(String) getAttrib(req, "name");
		if (name==null) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		if(!getChatData().hasChat(name)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
		}
		String msg=(String) getAttrib(req, "msg");
		if (msg==null) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		if((!msg.matches(Constants.ALLOWED_IN))||msg.length()>ChatProperties.getMaxChatCharactarsAllowedIn()){
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
		}
		if (!getChatData().renew(user)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
			
		}
		if (ChatData.endsWithSpaces(msg)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
		}
		getChatData().getChat(name).writeMsg("["+user+"] "+msg);
		//getChatData().saveChat(name, getChatData().getChat(name));
		resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
		//ReloadSocket.reloadAll();
		ReloadSocket.reload(name);
		return;
	}
}
