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
 * Servlet implementation class CreateChatServlet
 */
@WebServlet("/createChat")
public class CreateChatServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!assertUserPresent(req, resp)) {
			return;
		}
		String name=(String)getAttrib(req, "name");
		if (name==null) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		if(getChatData().hasChat(name)) {
//			req.setAttribute("chat", name);
//			req.getRequestDispatcher("/Chat.jsp").forward(req, resp);
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
		}
		
		if((!name.matches(Constants.ALLOWED_IN))||name.length()>ChatProperties.getMaxChatroomCharactarsAllowedIn()){
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		if (ChatData.endsWithSpaces(name)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		if (!getChatData().isNumOfChatsOK()) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		getChatData().createChat(name);
		resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name	);
		ReloadSocket.reloadAll();
//		req.setAttribute("chat", getAttrib(req, name));
//		req.getRequestDispatcher("/Chat.jsp").forward(req, resp);
		return;
	}
}
