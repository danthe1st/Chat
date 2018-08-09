package chat.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.utils.ChatProperties;
import chat.utils.Constants;

/**
 * Servlet implementation class RegistrationServlet
 */
@WebServlet("/register")
public class RegistrationServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (isBanned(req.getRemoteAddr())) {
			sendBanMsg(req, resp);
			return;
		}
		String user=getUser(req);
		if(user==null||user.equals("")) {
			sendErrMsg(req, resp, "missing user");
			return;
		}
		if((!user.matches(Constants.ALLOWED_IN))||user.length()>ChatProperties.getMaxChatCharactarsAllowedIn()){
			resp.sendRedirect(getServletContext().getContextPath());
			return;
		}
		String passwordHash=getPasswordHash(req);
		if(passwordHash==null||passwordHash.equals("")) {
			sendErrMsg(req, resp, "Please enter a password!");
			return;
		}
		
		if (!getHash((String)getAttrib(req,  Constants.PW_CONFIRM_FIELD), req.getSession()).equals(passwordHash)) {
			sendErrMsg(req, resp, "The Passwords you entered are not equal!");
			return;
		}
		if(hasUser(user)) {
			if(!login(req)) {
				sendErrMsg(req, resp, "User already registered");
				return;
			}
		}
		if (getChatData().getAllUsernames().size()>ChatProperties.getMaxUsers()) {
			sendErrMsg(req, resp, "Too many Users logged in!");
			return;
		}
		else {
			register(req);
			login(req);
		}
		resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
		return;
	}
}