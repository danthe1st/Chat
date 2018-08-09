package chat.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import chat.UserData;
/**
 * saves/loads Users with XML (JAXB)
 * @author Daniel Schmid
 */
public class XMLController {
	/**
	 * loads a user
	 * @param file the File to be loaded
	 * @return the user the loaded user
	 */
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
	/**
	 * saves a User to a File
	 * @param data the user
	 * @param file the File
	 */
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
