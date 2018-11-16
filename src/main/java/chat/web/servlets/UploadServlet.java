package chat.web.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import chat.ChatData;
import chat.utils.ChatProperties;
import chat.utils.Constants;
import chat.web.websockets.ReloadSocket;

/**
 * Servlet implementation class UploadServlet
 */
@MultipartConfig
@WebServlet("/Upload")
public class UploadServlet extends AbstractChatServlet{
	private static final long serialVersionUID = 1L;

    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!assertUserPresent(req, resp)) {
			return;
		}
		
		String user=getUser(req);
		String name=(String) getAttrib(req, "name");
		if (name==null) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		if (!ChatProperties.isFilesAllowed()) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
		}
		if(!getChatData().hasChat(name)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		if (!getChatData().renew(user)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
		}
		if (!getChatData().isNumFilesOK(user)) {
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
		}
		getChatData().addFile(user);
	    Part filePart = req.getPart("file"); // Retrieves <input type="file" name="file">
	    Path fileNamePath=Paths.get(extractFileName(filePart)).getFileName();
	    if (fileNamePath==null) {
	    	resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
	    	return;
		}
	    String fileName = fileNamePath.toString(); // MSIE fix.
	    if (fileName==null||fileName.equals("")) {
	    	resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
	    	return;
		}
	    InputStream fileContent = filePart.getInputStream();
	    try {
		    File path=new File(ChatData.pathname+"/Files/"+user);
		    if (!path.exists()) {
				path.mkdirs();
			}
		    File file=new File(path, fileName);
		    
		    if (!file.exists()) {
				file.createNewFile();
			}
		    try(FileOutputStream stream= new FileOutputStream(file)){
	        	byte[] buffer = new byte[1024];
	        	int len = fileContent.read(buffer);
	        	while (len != -1) {
	        	    stream.write(buffer, 0, len);
	        	    len = fileContent.read(buffer);
	        	}
	        }
	    }catch (IOException e) {
			e.printStackTrace();
		}
	    
	    String msg=getServletContext().getContextPath()+"/File?filename="+user+"/"+fileName;
		if((!msg.matches(Constants.ALLOWED_IN))||msg.length()>ChatProperties.getMaxChatCharactarsAllowedIn()){
			resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
			return;
		}
		
		getChatData().getChat(name).writeMsg("["+user+"] �"+msg+"�");
		//getChatData().saveChat(name, getChatData().getChat(name));
		resp.sendRedirect(getServletContext().getContextPath()+"/Chat?chat="+name);
		//ReloadSocket.reloadAll();
	    ReloadSocket.reload(name);
	    
	}

    
    
    
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";
    }
}
