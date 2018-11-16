package chat.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.utils.Constants;
import chat.web.websockets.ReloadSocket;

/**
 * Servlet implementation class DelMsg
 */
@WebServlet("/DelMsg")
public class DelMsgServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
       
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	if (!assertUserPresent(req, resp)) {
			return;
		}
    	if (!isAdmin(getUser(req))) {
    		resp.sendRedirect(req.getServletContext().getContextPath()+"/Chat");
    		return;
		}
    	if (!isManager(req)) {
			return;
		}
    	Object oMSGId= getAttrib(req, Constants.MSG_ID);
    	Object oChatId= getAttrib(req, Constants.CHAT_ID);
    	
    	if (oChatId==null||oMSGId==null) {
    		resp.sendRedirect(req.getServletContext().getContextPath()+"/Chat");
			return;
		}
    	try {
    		getChatData().getChat((String)oChatId).delMsg(Integer.parseInt((String)oMSGId));
		} catch (Exception e) {
			System.err.println("Error[deleting message]"+e.getMessage());
		}
    	resp.sendRedirect(req.getServletContext().getContextPath()+"/Chat");
    	//ReloadSocket.reloadAll();
    	ReloadSocket.reload((String)oChatId);
    }

}
