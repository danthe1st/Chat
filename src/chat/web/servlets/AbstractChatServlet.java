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
	public void sendBanMsg(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
		sendErrMsg(req, resp, "You are banned!");
	}
	public void sendNotLoginMsg(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
		sendErrMsg(req, resp, "You are not logged in!");
	}
	public void sendErrMsg(HttpServletRequest req,HttpServletResponse resp, String errmsg) throws ServletException, IOException {
		req.setAttribute(Constants.ERRMSG_FIELD, errmsg);
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
	}

	public ChatData getChatData()
	{
//		ChatData data=(ChatData) this.getServletContext().getAttribute(Constants.attributeChatData);
//		if(data==null)
//		{
////			data=ChatData.load();
////			if (data==null) {
//				data=new ChatData();
////			}
//			data.loadAll();
//			this.getServletContext().setAttribute(Constants.attributeChatData, data);
//		}
//		return data;
		return ChatData.getChatData(this.getServletContext());
	}
	
	public boolean hasUser(String user) {
		return getChatData().hasUser(user);
	}
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
	
	public String getUser(HttpServletRequest req) {
		String user=(String) getAttrib(req,Constants.UNAME_FIELD);
		return user;
	}
//	public int getPasswordHash(HttpServletRequest req) {
//		int pwHash=((String) getAttrib(req,Constants.PW_FIELD)).hashCode();
//		
//		return pwHash;
//
//	}
	public String getPasswordHash(HttpServletRequest req) {
		return getHash((String) getAttrib(req,Constants.PW_FIELD), req.getSession());
		//return Security.hash(Security.decryptRSA((String) getAttrib(req,Constants.PW_FIELD),req.getSession().getId()));
	}
	public String getHash(String rsaEncrypted, HttpSession session) {
		return Security.hash(Security.decryptRSA(rsaEncrypted,session.getId()));
	}
	public boolean isAdmin(String user) {
		return getChatData().isAdmin(user);
	}
	public void setAdmin(String user) {
		getChatData().setAdmin(user);
	}
	public void unsetAdmin(String user) {
		getChatData().unsetAdmin(user);
	}
	
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
	
	public boolean login(HttpServletRequest req) {
		String username=getUser(req);
		String pwHash=getPasswordHash(req);
		if (username==null) {
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
	public void logout(HttpServletRequest req) {
		logout(getUser(req));
		HttpSession session=req.getSession(false);
		if (session!=null) {
			session.invalidate();
		}
		
	}
	public void logout(String user) {
		getChatData().logout(user);
	}
	
	public boolean register(HttpServletRequest req) {
		String username=getUser(req);
		String pwHash=getPasswordHash(req);
		if (hasUser(username)) {
			return login(req);
		}
		String pwConfHash=getHash((String)getAttrib(req,  Constants.PW_CONFIRM_FIELD), req.getSession());
		getChatData().registerUser(username, pwHash,pwConfHash,req.getRemoteAddr());
		getSession(req).setAttribute(Constants.UNAME_FIELD, username);		
		return true;
	}
	public void unregister(String user) {
		getChatData().unregisterUser(user);
	}
	
	protected HttpSession getSession(HttpServletRequest req) {
		HttpSession session=req.getSession(false);
		if (session==null) {
			session=req.getSession(true);
		}
		return session;
	}
	public void ban(String ip) {
		getChatData().ban(ip);
	}
	public void unban(String ip) {
		getChatData().unban(ip);
	}
	public boolean isBanned(String ip) {
		return getChatData().isBanned(ip);
	}
	protected boolean isManeger(HttpServletRequest req) {
    	if(getSession(req).getAttribute(Constants.MANAGER_FIELD)!=null) {
    		return (boolean) getSession(req).getAttribute(Constants.MANAGER_FIELD);
    	}
    	return false;
    }
}
