package salesforce.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

@Service
public class ConnectService {
	
	
	private String clientId="3MVG9d8..z.hDcPINdzSLc.18_81q0lUvUfw0Iac.ZFL.ZtuWLr_wlXVtzKGPDTk.7Az6sf61wbcwCLqsrDIU";
	private String clientSecret="4436621224688664918";
	//private String redirectUri="http://localhost:8080/force_rest_example/oauth/callback";
	private String redirectUri="http://localhost:8080/retail_salesforce/salesforce/callback";
	private String environment="https://login.salesforce.com";
	
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";
	
	private String authUrl = null;
//	private String tokenUrl = null;
//	private String accessToken=null;
	
	
	public void getConnectInfo2Salesforce(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		 System.out.println("in getConnectInfo2Salesforce");
		
		authUrl = environment
				+ "/services/oauth2/authorize?response_type=code&client_id="
				+ clientId + "&redirect_uri="
				+ URLEncoder.encode(redirectUri, "UTF-8");
		
		String	tokenUrl = environment + "/services/oauth2/token";
		
		String accessToken = (String) request.getSession().getAttribute(
				ACCESS_TOKEN);
		
		System.out.println("request uri --> "+request.getRequestURI());
		if (accessToken == null) {
			String instanceUrl = null;
			

			if (request.getRequestURI().endsWith("connect") || request.getRequestURI().contains("/show")) {
				System.out.println("--------------------------->"+request.getRequestURI());	
				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			}
			// Set a session attribute so that other servlets can get the access
			// token
			request.getSession().setAttribute(ACCESS_TOKEN, accessToken);

			// We also get the instance URL from the OAuth response, so set it
			// in the session too
			request.getSession().setAttribute(INSTANCE_URL, instanceUrl);
		}
		
	//	return null;
		
	}



}
