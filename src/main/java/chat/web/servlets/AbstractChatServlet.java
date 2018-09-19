package chat.web.servlets;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import chat.ChatData;
import chat.utils.Constants;
import chat.utils.Security;

public class AbstractChatServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	/**
	 * checks if there is the user sending a request is logged in
	 * @param req the Request
	 * @param resp the (future) response of the Server
	 * @return <code>true</code> if the user is present and the ip not banned
	 * @throws IOException if something happened
	 * @throws ServletException if something happened
	 */
	public boolean assertUserPresent(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException {
		if (!isLoggedIn(req)) {
			sendNotLoginMsg(req, resp);
			return false;
		}
		if (isBanned(req.getRemoteAddr())) {
			sendBanMsg(req, resp);
			return false;
		}
		return true;
	}
	/**
	 * send a response that the user is banned.
	 * @param req the Request
	 * @param resp the (future) response of the Server
	 * @throws IOException if something happened
	 * @throws ServletException if something happened
	 */
	public void sendBanMsg(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
		sendErrMsg(req, resp, "You are banned!");
	}
	/**
	 * send a response that the user is not logged in when trying to access something witch needs the user to be logged in.
	 * @param req the Request
	 * @param resp the (future) response of the Server
	 * @throws IOException if something happened
	 * @throws ServletException if something happened
	 */
	public void sendNotLoginMsg(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
		sendErrMsg(req, resp, "You are not logged in!");
	}
	/**
	 * sends a (error)-Message to the Client
	 * @param req the Request
	 * @param resp the (future) response of the Server
	 * @param errmsg the Error Message
	 * @throws IOException if something happened
	 * @throws ServletException if something happened
	 */
	public void sendErrMsg(HttpServletRequest req,HttpServletResponse resp, String errmsg) throws ServletException, IOException {
		req.setAttribute(Constants.ERRMSG_FIELD, errmsg);
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}
	/**
	 * returns the {@link ChatData} representation
	 * @return the ChatData
	 */
	public ChatData getChatData()
	{
		return ChatData.getChatData(this.getServletContext());
	}
	/**
	 * checks if there is a user with a specified username
	 * @param user the username
	 * @return <code>true</code> if there is a user with this name
	 */
	public boolean hasUser(String user) {
		return getChatData().hasUser(user);
	}
	/**
	 * checks if a user is logged in when requesting something
	 * @param req the {@link HttpServletRequest} representation of the Request
	 * @return <code>true</code> if the user is logged in
	 */
	public boolean isLoggedIn(HttpServletRequest req) {
		HttpSession session= getSession(req);
		
		String user=(String) session.getAttribute(Constants.UNAME_FIELD);
		if(user==null) {
			return false;
		}
		if (!getChatData().hasUser(user)) {
			return false;
		}
		return getChatData().isLoggedIn(user);
		
	}
	/**
	 * gets the username from a {@link HttpServletRequest}
	 * @param req the Request sended by the Client
	 * @return the username of the user
	 */
	public String getUser(HttpServletRequest req) {
		String user=(String) getAttrib(req,Constants.UNAME_FIELD);
		return user;
	}
	/**
	 * gets the password hash from an {@link HttpServletRequest}
	 * @param req the Request sended by the Client
	 * @return the password hash
	 */
	public String getPasswordHash(HttpServletRequest req) {
		return getHash(getUser(req), (String) getAttrib(req,Constants.PW_FIELD), req.getSession());
	}
	/**
	 * gets the password hash using the username, the RSA-encrypted password and the {@link HttpSession}<br>
	 * the username is needed that multiple users with the same password do not have the same hash<br>
	 * the {@link HttpSession} is needed for the RSA decryption
	 * @param username the username of the user from the hash
	 * @param rsaEncrypted the RSA encrypted password
	 * @param session the {@link HttpSession} the user uses for his login and things like that
	 * @return the password-hash or null if any of the parameters are invalid
	 */
	public String getHash(String username, String rsaEncrypted, HttpSession session) {
		if (username==null||username.equals("")||rsaEncrypted==null||rsaEncrypted.equals("")) {
			return null;
		}
		String decrypted=Security.decryptRSA(rsaEncrypted,session);
		if (decrypted==null) {
			return null;
		}
		return Security.hash(decrypted+username);
	}
	/**
	 * checks if a user is an Admin
	 * @param user the username of the user
	 * @return <code>true</code> if the user is an Admin
	 */
	public boolean isAdmin(String user) {
		return getChatData().isAdmin(user);
	}
	/**
	 * gives a user Admin permissions
	 * @param user the username of the user
	 */
	public void setAdmin(String user) {
		getChatData().setAdmin(user);
	}
	/**
	 * revokes Admin permissions from a user
	 * @param user the username of the user
	 */
	public void unsetAdmin(String user) {
		getChatData().unsetAdmin(user);
	}
	/**
	 * gets an Attribute from the Client request
	 * @param req the Client Request
	 * @param attribName the name of the Attribute
	 * @return the Attribute or null if it was not found
	 */
	public Object getAttrib(HttpServletRequest req, String attribName) {
		
		Object attrib=req.getParameter(attribName);
		if (attrib==null) {
			attrib=req.getAttribute(attribName);
		}
		if (attrib==null) {
			attrib=(getSession(req).getAttribute(attribName));
		}
		if (attrib==null) {
			try {
				for (String elements : req.getQueryString().split("&")) {
					if(elements.split("=")[0].equals(attribName)) {
						attrib=elements.substring(elements.indexOf("=",elements.length()));
					}
				}
			} catch (Exception e) {
			}	
		}
		return attrib;
	}
	/**
	 * loggs a user in
	 * @param req the Client request of the user
	 * @return <code>true</code> if it worked
	 */
	public boolean login(HttpServletRequest req) {
		String username=getUser(req);
		String pwHash=getPasswordHash(req);
		if (username==null) {
			return false;
		}
		if (pwHash==null) {
			return false;
		}
		if(getChatData().isPasswordCorrect(username, pwHash)) {
			getSession(req).setAttribute(Constants.UNAME_FIELD, username);
			getSession(req).setAttribute(Constants.PW_FIELD, pwHash);
			getChatData().login(username);
			return true;
		}
		return false;
	}
	/**
	 * logges a user out
	 * @param req the Client request
	 */
	public void logout(HttpServletRequest req) {
		logout(getUser(req));
		HttpSession session=req.getSession(false);
		if (session!=null) {
			session.invalidate();
		}
	}
	/**
	 * loggs a user out
	 * @param user the username of the user
	 */
	public void logout(String user) {
		getChatData().logout(user);
	}
	/**
	 * registeres a user
	 * @param req the Client request
	 * @return <code>true</code> if it worked
	 */
	public boolean register(HttpServletRequest req) {
		String username=getUser(req);
		String pwHash=getPasswordHash(req);
		if (pwHash==null) {
			return false;
		}
		if (hasUser(username)) {
			return login(req);
		}
		String pwConfHash=getHash(getUser(req), (String)getAttrib(req,  Constants.PW_CONFIRM_FIELD), req.getSession());
		if(!getChatData().registerUser(username, pwHash,pwConfHash,req.getRemoteAddr())) {
			return false;
		}
		getSession(req).setAttribute(Constants.UNAME_FIELD, username);		
		return true;
	}
	/**
	 * deletes an user
	 * @param user the username of the user
	 */
	public void unregister(String user) {
		getChatData().unregisterUser(user);
	}
	/**
	 * gets the {@link HttpSession} from a Client request
	 * @param req the Client request
	 * @return the Session
	 */
	protected HttpSession getSession(HttpServletRequest req) {
		HttpSession session=req.getSession(false);
		if (session==null) {
			session=req.getSession(true);
		}
		return session;
	}
	/**
	 * bans an ip
	 * @param ip the IP-Address to be banned
	 */
	public void ban(String ip) {
		getChatData().ban(ip);
	}
	/**
	 * unbans an ip
	 * @param ip the IP-Adress to be unbanned
	 */
	public void unban(String ip) {
		getChatData().unban(ip);
	}
	/**
	 * checks if an ip is banned
	 * @param ip the IP-Adress
	 * @return <code>true</code> if the IP-Adress is banned
	 */
	public boolean isBanned(String ip) {
		return getChatData().isBanned(ip);
	}
	/**
	 * checks if a user has the Manager mode enabled
	 * @param req the Client request
	 * @return <code>true</code> if the Manager mode is enabled for this user
	 */
	protected boolean isManager(HttpServletRequest req) {
    	if(getSession(req).getAttribute(Constants.MANAGER_FIELD)!=null) {
    		return (boolean) getSession(req).getAttribute(Constants.MANAGER_FIELD);
    	}
    	return false;
    }
}
