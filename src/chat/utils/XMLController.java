package chat.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import chat.UserData;

public class XMLController {
	public static UserData loadUser(File file) {
		try {
			JAXBContext context=JAXBContext.newInstance(UserData.class);
			 Unmarshaller um = context.createUnmarshaller();

		        // Reading XML from the file and unmarshalling.
		        UserData data = (UserData) um.unmarshal(file);
		        
		        return data;
		} catch (JAXBException e) {
			return null;
		}
	}
	public static void saveUser(UserData data,File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		try {
			JAXBContext context = JAXBContext
			        .newInstance(UserData.class);
			Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        m.marshal(data, file);
		} catch (JAXBException e) {
			
		}
       

	}
}
