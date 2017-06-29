package salesforce.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

@Service
public class OperatiosService {

	public void showAccounts(String instanceUrl, String accessToken, PrintWriter writer, Map<String, Object> paramMap)
			throws ServletException, IOException {
		
		System.out.println("in showAccounts");
		
		String tableName=(String) paramMap.get("table");
		
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet();

		// add key and value
		httpGet.addHeader("Authorization", "OAuth " + accessToken);

		try {
			System.out.println("instanceUrl   " + instanceUrl);

			URIBuilder builder = new URIBuilder(instanceUrl + "/services/data/v30.0/query");
			builder.setParameter("q", "SELECT Name, Id from "+tableName+" LIMIT 100");

			httpGet.setURI(builder.build());

			CloseableHttpResponse closeableresponse = httpclient.execute(httpGet);
			System.out.println("Response Status line :" + closeableresponse.getStatusLine());

			if (closeableresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the
				// results
				try {

					// Do the needful with entity.
					HttpEntity entity = closeableresponse.getEntity();
					InputStream rstream = entity.getContent();
					JSONObject authResponse = new JSONObject(new JSONTokener(rstream));

					System.out.println("Query response: " + authResponse.toString(2));

					writer.write(authResponse.getInt("totalSize") + " record(s) returned\n\n");

					JSONArray results = authResponse.getJSONArray("records");

					for (int i = 0; i < results.length(); i++) {
						writer.write(results.getJSONObject(i).getString("Id") + ", "
								+ results.getJSONObject(i).getString("Name") + "\n");
					}
					writer.write("\n");
				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		} catch (URISyntaxException e1) {
			
			e1.printStackTrace();
		} finally {
			httpclient.close();
		}
	}

	public void operations(String instanceUrl, String accessToken, PrintWriter writer, String operation, Map<String, Object> paramMap) throws ServletException, IOException {
		
		if("show".equals(operation)){
			showAccounts(instanceUrl, accessToken, writer,paramMap);
		}
		else if("delete".equals(operation)){
			System.out.println("delete will be done now");
			
		}
		
	}

}
