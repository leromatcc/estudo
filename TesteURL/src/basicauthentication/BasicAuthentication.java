package basicauthentication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

public class BasicAuthentication {

	String webPage = "";;
	String name = "";
	String password = "";

	public BasicAuthentication() {
		this("http://192.168.0.150/dms", "placas", "reconhecer");
	}

	public BasicAuthentication(String webPage, String name, String password) {
		this.webPage = webPage;
		this.name = name;
		this.password = password;
	}

	public String criarAuthStringEncoded() {
		String authString = name + ":" + password;
		System.out.println("auth string: " + authString);
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		System.out.println("Base64 encoded auth string: " + authStringEnc);
		return authStringEnc;
	}

	public void conectar() {

		try {
			InputStream is1  = generateInputStream(this.webPage, criarAuthStringEncoded());
			String destinationFile = "capturas1/image" + System.currentTimeMillis() + ".jpg";
			OutputStream os1 = generateOutputStream(destinationFile);
			saveStream(is1, os1);
		}	catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			
			InputStreamReader isr = new InputStreamReader(generateInputStream(this.webPage, criarAuthStringEncoded()));

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();

			System.out.println("*** BEGIN ***");
			System.out.println(result);
			System.out.println("*** END ***");

			String destinationFile = "capturas/image" + System.currentTimeMillis() + ".jpg";
/*			OutputStream os = generateOutputStream(destinationFile);
			os.write(b);
*/
			OutputStream output = new FileOutputStream(destinationFile);

			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output) ) {
				outputStreamWriter.append(sb);
				//outputStreamWriter.writeObject(person);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InputStream generateInputStream(String streamUrl, String authStringEncoded) throws IOException {
		URL url = new URL(streamUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + authStringEncoded);
		InputStream is = urlConnection.getInputStream();
		return is;
	}

	public OutputStream generateOutputStream(String destinationFile) throws IOException {
		OutputStream os = new FileOutputStream(destinationFile);
		return os;
	}

	public void saveStream(InputStream is, OutputStream os) throws IOException {

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}
}
