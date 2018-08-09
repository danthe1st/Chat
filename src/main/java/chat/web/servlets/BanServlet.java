package chat.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.utils.Constants;
import chat.web.websockets.ReloadSocket;

/**
 * Servlet implementation class BanServlet
 */
@WebServlet("/ban")
public class BanServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	if (!assertUserPresent(req, resp)) {
			return;
		}
    	if (!isAdmin(getUser(req))) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
    	String user=(String) getAttrib(req, Constants.BAN_FIELD);
    	try {
    		if (isBanned(getChatData().getIp(user))) {
				unban(getChatData().getIp(user));
			}
    		ban(getChatData().getIp(user));
		} catch (Exception e) {}
    	
    	resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
    	ReloadSocket.reloadAll();
    }

}
