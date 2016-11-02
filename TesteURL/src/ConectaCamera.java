import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class ConectaCamera {
	
	public static void main(String[] args) throws Exception {
		ConectaCamera cc = new ConectaCamera();
		cc.testar();
		
	}
	
	public void testar() throws Exception{
		String scope = "192.168.0.150:80"; 
		int port = 80;
		String username = "placas"; 
		String password = "reconhecer";
		// String endpoint = "http://"+scope+":"+port+"/dms?nowprofileid=2";
		String endpoint = "http://"+scope+":"+port+"/image/jpeg.cgi?user=placas&password=reconhecer";
		
		CredentialsProvider credsProvider = criaCredenciais(scope, port, username, password );
		
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		

		/*
		 * 
Host: 192.168.0.150
User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,* /*;q=0.8
Accept-Language: en-US,pt-BR;q=0.8,en;q=0.5,pt;q=0.3
Accept-Encoding: gzip, deflate
DNT: 1
Authorization: Basic cGxhY2FzOnJlY29uaGVjZXI=
Connection: keep-alive
Upgrade-Insecure-Requests: 1
Cache-Control: max-age=0
		 * */
		
		try {
			HttpGet httpget = new HttpGet(endpoint);


			System.out.println("Executing request " + httpget.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				System.out.println(EntityUtils.toString(response.getEntity()));
			} finally {
				response.close();
			}
		} catch(HttpHostConnectException ce){
			System.out.println("Ocorreu uma exceção ao conectar");
			ce.printStackTrace();
		} finally {
			httpclient.close();
		}		
	}

	private CredentialsProvider criaCredenciais(String scope, int port, String username, String password) {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(scope, port),
									 new UsernamePasswordCredentials(username, password)
									 );
		return credsProvider;
	}
	
}
