
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class AuthEncoding {

	public void configura() {
		String endpoint = "http://192.168.0.150:80/dms?nowprofileid=2";
		String username = "placas";
		String password = "reconhecer";

		HttpGet getRequest = getHttpGetRequest(endpoint, username, password);

	}

	public HttpGet getHttpGetRequest(String endpoint, String username, String password) {
		HttpGet getRequest = new HttpGet(endpoint);
		getRequest.addHeader("Authorization", "Basic " + getBasicAuthenticationEncoding(username, password));
		return getRequest;
	}

	public String getBasicAuthenticationEncoding(String username, String password) {

		String userPassword = username + ":" + password;
		return new String(Base64.encodeBase64(userPassword.getBytes()));
	}

	public InputStream generateInputStream(String streamUrl)  throws IOException {
		URL url = new URL(streamUrl);
		InputStream is = url.openStream();
		return is;
	}
	
	public OutputStream generateOutputStream(String destinationFile) throws IOException{
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

	public void lkjhsdafljhgsdf() throws Exception {
		URL oracle = new URL("http://www.uol.com.br/");
		URLConnection yc = oracle.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();

	}

/*	public void kjçhakjsdkjh() throws Exception{
		URL url = new URL("location address");
		URLConnection uc = url.openConnection();
		String userpass = username + ":" + password;
		String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
		uc.setRequestProperty ("Authorization", basicAuth);
		InputStream in = uc.getInputStream();
	}*/
	
	
}
