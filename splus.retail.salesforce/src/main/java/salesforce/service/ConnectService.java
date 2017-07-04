package salesforce.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

import salesforce.util.SFUtil;

@Service
public class ConnectService {

	@Autowired
	SFUtil sfutil;

	private String clientId = "3MVG9d8..z.hDcPINdzSLc.18_81q0lUvUfw0Iac.ZFL.ZtuWLr_wlXVtzKGPDTk.7Az6sf61wbcwCLqsrDIU";
	private String clientSecret = "4436621224688664918";
	// private String
	// redirectUri="http://localhost:8080/force_rest_example/oauth/callback";
	private String redirectUri = "http://localhost:8080/retail_salesforce/salesforce/callback";
	private String environment = "https://login.salesforce.com";

	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";

	private String authUrl = null;
	// private String tokenUrl = null;
	// private String accessToken=null;

	public void getConnectInfo2Salesforce(HttpServletRequest request, HttpServletResponse response) throws IOException {

		System.out.println("in getConnectInfo2Salesforce");

		authUrl = environment + "/services/oauth2/authorize?response_type=code&client_id=" + clientId + "&redirect_uri="
				+ URLEncoder.encode(redirectUri, "UTF-8");

		String tokenUrl = environment + "/services/oauth2/token";

		String accessToken = (String) request.getSession().getAttribute(ACCESS_TOKEN);

		System.out.println("request uri --> " + request.getRequestURI());
		if (accessToken == null) {
			String instanceUrl = null;

			if (request.getRequestURI().endsWith("connect") || request.getRequestURI().contains("/show")
					|| request.getRequestURI().contains("/test")) {
				System.out.println("--------------------------->" + request.getRequestURI());
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			} else {
				tokenUrl = environment + "/services/oauth2/token";

				System.out.println("Auth successful - got callback");

				String code = request.getParameter("code");

				System.out.println("code " + code);

				// Create an instance of HttpClient.
				CloseableHttpClient httpclient = HttpClients.createDefault();

				try {
					// Create an instance of HttpPost.
					HttpPost httpost = new HttpPost(tokenUrl);

					// Adding all form parameters in a List of type
					// NameValuePair

					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("code", code));
					nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
					nvps.add(new BasicNameValuePair("client_id", clientId));
					nvps.add(new BasicNameValuePair("client_secret", clientSecret));
					nvps.add(new BasicNameValuePair("redirect_uri", redirectUri));

					httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

					// Execute the request.
					CloseableHttpResponse closeableresponse = httpclient.execute(httpost);
					System.out.println("Response Status line :" + closeableresponse.getStatusLine());
					try {
						// Do the needful with entity.
						HttpEntity entity = closeableresponse.getEntity();
						InputStream rstream = entity.getContent();
						JSONObject authResponse = new JSONObject(new JSONTokener(rstream));

						System.out.println("rstream.toString()--->" + rstream.toString());
						System.out.println("authResponse--->" + authResponse);

						accessToken = authResponse.getString("access_token");
						instanceUrl = authResponse.getString("instance_url");

						String refreshToken = authResponse.getString("refresh_token");

						System.out.println("refresh_token :  " + refreshToken);

						System.out.println("accessToken  " + accessToken);
						System.out.println("instanceUrl " + instanceUrl);

						request.setAttribute("ACCESS_TOKEN", accessToken);
						request.setAttribute("INSTANCE_URL", instanceUrl);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						// Closing the response
						closeableresponse.close();
					}
				} finally {
					httpclient.close();
				}

				System.out.println("accessToken (here at the end)--> " + accessToken);
				System.out.println("instanceUrl (here at the end)--> " + instanceUrl);

				System.out.println("send redirect url--> " + request.getRequestURI());
				response.sendRedirect("/retail_salesforce/salesforce/operation?token=" + accessToken + "&instanceUrl="
						+ instanceUrl + "");

			}

			// Set a session attribute so that other servlets can get the access
			// token
			request.getSession().setAttribute(ACCESS_TOKEN, accessToken);

			// We also get the instance URL from the OAuth response, so set it
			// in the session too
			request.getSession().setAttribute(INSTANCE_URL, instanceUrl);
		}

		// return null;

	}

	public Map<String, String> connectWithRefreshToken(String refreshToken) {

		String LOGINURL = "https://ap5.salesforce.com";
		String GRANTSERVICE = "/services/oauth2/token?grant_type=refresh_token";
		String CLIENTID = "3MVG9d8..z.hDcPINdzSLc.18_81q0lUvUfw0Iac.ZFL.ZtuWLr_wlXVtzKGPDTk.7Az6sf61wbcwCLqsrDIU";
		String CLIENTSECRET = "4436621224688664918";
		StringBuilder msg = new StringBuilder();
		Map<String, String> connectParam = new HashMap<String, String>();

		HttpClient httpclient = HttpClientBuilder.create().build();
		String loginURL = LOGINURL + GRANTSERVICE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET
				+ "&refresh_token=" + refreshToken;
		System.out.print(loginURL);

		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse response = null;
		try {
			// Execute the login POST request
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException cpException) {
			cpException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			msg = msg.append(statusCode);
			System.out.println("Error authenticating to Force.com: " + statusCode);
			// Error is in EntityUtils.toString(response.getEntity())

		}

		String getResult = null;
		try {
			getResult = EntityUtils.toString(response.getEntity());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		JSONObject jsonObject = null;
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			loginAccessToken = jsonObject.getString("access_token");
			loginInstanceUrl = jsonObject.getString("instance_url");
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}
		System.out.println(response.getStatusLine());
		System.out.println("Successful login");
		System.out.println("instance URL: " + loginInstanceUrl);
		System.out.println("access token/session ID: " + loginAccessToken);

		msg = msg.append(response.getStatusLine()).append("    ").append("Successful login   ").append(loginInstanceUrl)
				.append("  ").append(loginAccessToken);

		// release connection
		httpPost.releaseConnection();

		System.out.println("MSG:  " + msg);
		connectParam.put("INSTANCE_URL", loginInstanceUrl);
		connectParam.put("ACCESS_TOKEN", loginAccessToken);

		return connectParam;

	}

	public Map<String, String> getConnectParam() {

		Map<String, String> connectParam = new HashMap<String, String>();

		String refreshToken = sfutil.getRefreshToken();

		if (refreshToken != null || refreshToken != "") {

			connectParam = connectWithRefreshToken(refreshToken);

		}

		return connectParam;

	}

}
