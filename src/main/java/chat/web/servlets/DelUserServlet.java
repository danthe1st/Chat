package chat.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.utils.Constants;
import chat.web.websockets.ReloadSocket;

/**
 * Servlet implementation class DelUserServlet
 */
@WebServlet("/delUser")
public class DelUserServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!assertUserPresent(req, resp)) {
			return;
		}
		
		String user=(String) getAttrib(req, Constants.UNAME_FIELD);
		if (!isAdmin(user)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		String unameDel=(String)getAttrib(req, Constants.UNAME_DEL_FIELD);
		if (unameDel==null) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		unregister(unameDel);
		
		resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
		ReloadSocket.reloadAll();
		return;
	}

}
