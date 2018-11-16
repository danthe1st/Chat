package chat.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.utils.Constants;
import chat.web.websockets.ReloadSocket;

/**
 * Servlet implementation class KickServlet
 */
@WebServlet("/kick")
public class KickServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!assertUserPresent(req, resp)) {
			return;
		}
		String user=(String) getAttrib(req, Constants.UNAME_FIELD);
		if (!isAdmin(user)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		String unameKick=(String)getAttrib(req, Constants.UNAME_KICK_FIELD);
		if (unameKick==null) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		logout(unameKick);
		
		resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
		ReloadSocket.reloadAll();
		return;
	}

}
