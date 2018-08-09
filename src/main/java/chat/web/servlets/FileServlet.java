package chat.web.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.ChatData;
import chat.utils.ChatProperties;

/**
 * Servlet implementation class FileServlet
 */
@WebServlet("/File")
public class FileServlet extends AbstractChatServlet {
	private static final long serialVersionUID = 1L;
       
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	if (!assertUserPresent(req, resp)) {
			return;
		}
    	if (!ChatProperties.isFilesAllowed()) {
    		resp.sendRedirect(getServletContext().getContextPath()+"/Chat");
			return;
		}
		String name=(String) getAttrib(req, "filename");
		File file=new File(ChatData.pathname+"/Files/"+name);
		if (!file.exists()) {
			return;
		}
		FileInputStream fis=new FileInputStream(file);
		String mimeType = getServletContext().getMimeType(file.getAbsolutePath());
		resp.setContentType(mimeType != null? mimeType:"application/octet-stream");
		resp.setContentLength((int) file.length());
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
		
		ServletOutputStream os = resp.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read=0;
		while((read = fis.read(bufferData))!= -1){
			os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();
    }
}
