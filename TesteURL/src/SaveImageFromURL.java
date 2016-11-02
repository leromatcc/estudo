import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;

public class SaveImageFromURL {

	public static void main(String[] args) throws Exception {
		//
		SaveImageFromURL sifu = new SaveImageFromURL();
		//
		
		
		String endpoint = "http://192.168.0.150/dms?nowprofileid=2";
		HttpGet getRequest = new HttpGet(endpoint);
		getRequest.addHeader("Authorization", "Basic " + sifu.getBasicAuthenticationEncoding("placas","reconhecer"));

		/*URLConnection urlConnection = url.openConnection();
		InputStream inputStream = urlConnection.getInputStream();
		*/
		
		
		String imageUrl = "http://www.avajava.com/images/avajavalogo.jpg";
		String destinationFile = "captura/image.jpg";

		saveImage(imageUrl, destinationFile);
	}
	
	private String getBasicAuthenticationEncoding(String username, String password) {

        String userPassword = username + ":" + password;
        return new String(Base64.encodeBase64(userPassword.getBytes()));
    }
	
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
		URL url = new URL(imageUrl);

		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}

}