package salesforce.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import salesforce.entity.UpdateParam;
import salesforce.util.SFUtil;

@Service
public class SFService {

	@Autowired
	SFUtil sfutil;

	public String display(String instanceUrl, String accessToken, String table) throws ServletException, IOException {

		System.out.println("in display entity");

		String outputJson = "";

		String tableName = table;

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet();

		// add key and value
		httpGet.addHeader("Authorization", "OAuth " + accessToken);

		try {
			System.out.println("instanceUrl   " + instanceUrl);

			URIBuilder builder = new URIBuilder(instanceUrl + "/services/data/v30.0/query");
			builder.setParameter("q", "SELECT Name, Id, BillingStreet, BillingCity from " + tableName + " LIMIT 100");

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

					outputJson = authResponse.toString(2);

					
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

		return outputJson;

	}

	public String delete(String instanceUrl, String accessToken, Map<String, Object> paramMap)
			throws ClientProtocolException, IOException, URISyntaxException {

		CloseableHttpClient httpclient = HttpClients.createDefault();

		String status = "";

		String table = (String) paramMap.get("table");
		String id = (String) paramMap.get("id");

		System.out.println("table " + table + "   id" + id);
		System.out.println("-->" + instanceUrl + "/services/data/v30.0/sobjects/Account/" + id);

		HttpDelete delete = new HttpDelete(instanceUrl + "/services/data/v30.0/sobjects/Account/" + id);

		/*
		 * HttpDelete delete = new HttpDelete(instanceUrl +
		 * "/services/data/v30.0/sobjects/"+table+"/" + id);
		 * 
		 * delete.setHeader("Authorization", "OAuth " + accessToken);
		 */
		HttpGet httpGet = new HttpGet();

		// add key and value
		httpGet.addHeader("Authorization", "OAuth " + accessToken);

		URIBuilder builder = new URIBuilder(instanceUrl + "/services/data/v30.0/query");
		builder.setParameter("q", "Delete from " + table + " where id=" + id);

		httpGet.setURI(builder.build());

		CloseableHttpResponse closeableresponse = httpclient.execute(httpGet);
		System.out.println("Response Status line :" + closeableresponse.getStatusLine());

		/*
		 * // Execute the request. CloseableHttpResponse closeableresponse =
		 * httpclient.execute(delete); System.out.println(
		 * "Response Status line :" + closeableresponse.getStatusLine());
		 * 
		 * try { status="HTTP status " +
		 * closeableresponse.getStatusLine().getStatusCode() +
		 * " deleting account " + id + "\n\n"; } finally {
		 * delete.releaseConnection(); }
		 */
		return status;

	}

	public String createByName(String instanceUrl, String accessToken, InputStream myjson)
			throws IOException, ServletException {
		System.out.println("in service create ...");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String resultJson = "";

		paramMap = sfutil.getParamMapForCreateByName(myjson);

		String entityId = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();

		JSONObject objEntity = new JSONObject();

		System.out.println("table Name  " + paramMap.get("table"));
		System.out.println("Name  " + paramMap.get("name"));

		try {
			objEntity.put("Name", paramMap.get("name"));
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}

		System.out.println("post url " + instanceUrl + "/services/data/v30.0/sobjects/" + paramMap.get("table") + "/");

		HttpPost httpost = new HttpPost(instanceUrl + "/services/data/v30.0/sobjects/" + paramMap.get("table") + "/");
		// HttpPost httpost = new HttpPost(instanceUrl +
		// "/services/data/v30.0/sobjects/Contact/");

		httpost.addHeader("Authorization", "OAuth " + accessToken);

		StringEntity messageEntity = new StringEntity(objEntity.toString(),
				ContentType.create("application/json", "UTF-8"));

		httpost.setEntity(messageEntity);

		// Execute the request.
		CloseableHttpResponse closeableresponse = httpclient.execute(httpost);
		System.out.println("Response Status line :" + closeableresponse.getStatusLine());

		try {

			System.out.println(
					"HTTP status " + closeableresponse.getStatusLine().getStatusCode() + " creating account\n\n");

			if (closeableresponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				try {

					// Do the needful with entity.
					HttpEntity entity = closeableresponse.getEntity();
					InputStream rstream = entity.getContent();
					JSONObject authResponse = new JSONObject(new JSONTokener(rstream));

					System.out.println("Create response: " + authResponse.toString(2));
					resultJson = authResponse.toString(2);

					if (authResponse.getBoolean("success")) {
						entityId = authResponse.getString("id");
						System.out.println("New record id " + entityId + "\n\n");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					// throw new ServletException(e);
				}
			}
		} finally {
			httpclient.close();
		}

		return resultJson;

	}

	public String descEntity(String instanceUrl, String accessToken, String table)
			throws ClientProtocolException, IOException, ServletException {

		System.out.println("in showAccounts");

		String outputJson = "";
		// Map<String, Object> paramMap = new HashMap<String, Object>();
		// paramMap = sfutil.getParamMapForEntity(table);

		// String tableName = (String) paramMap.get("table");

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(instanceUrl + "/services/data/v30.0/sobjects/" + table + "/describe/");

		// add key and value
		httpGet.addHeader("Authorization", "OAuth " + accessToken);

		try {
			System.out.println("instanceUrl   " + instanceUrl);

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

					outputJson = authResponse.toString(2);

				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		} finally {
			httpclient.close();
		}

		return outputJson;
	}

	public String listAllObjects(String instanceUrl, String accessToken)
			throws ClientProtocolException, IOException, ServletException {

		String outputJson = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(instanceUrl + "/services/data/v37.0/sobjects/");

		// add key and value
		httpGet.addHeader("Authorization", "OAuth " + accessToken);

		try {
			System.out.println("instanceUrl   " + instanceUrl);

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

					outputJson = authResponse.toString(2);

				} catch (JSONException e) {
					e.printStackTrace();
					throw new ServletException(e);
				}
			}
		} finally {
			httpclient.close();
		}

		return outputJson;

	}

	

	public String updateById(String instanceUrl, String accessToken, InputStream myjson) throws ServletException, ClientProtocolException, IOException {
		
		CloseableHttpClient httpclient = HttpClients.createDefault(); 
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String resultJson = "";

		UpdateParam param = sfutil.getParamMapForUpdateById(myjson);
		

		JSONObject update = new JSONObject();
		
		String accountId="0017F000007etAJQAY";

		try {
			update.put("Name", "Manas city");
			update.put("BillingCity", "Guwahati");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
		
		System.out.println("upadte json object"+update.toString());

		HttpPost httpost = new HttpPost(instanceUrl
				+ "/services/data/v30.0/sobjects/Account/" +accountId+"?_HttpMethod=PATCH");  


		httpost.addHeader("Authorization", "OAuth " + accessToken);


		StringEntity messageEntity = new StringEntity( update.toString(),
				ContentType.create("application/json"));

		httpost.setEntity(messageEntity);



		// Execute the request.  
		CloseableHttpResponse closeableresponse = httpclient.execute(httpost);  
		System.out.println("Response Status line :" + closeableresponse.getStatusLine());  


		try {
			
			System.out.println("HTTP status " + closeableresponse.getStatusLine().getStatusCode()
					+ " updating account " + accountId + "\n\n");
		} finally {
			httpclient.close();

		}
		return accountId;
	}

}
