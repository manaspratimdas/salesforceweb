package salesforce.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import salesforce.service.ConnectService;
import salesforce.service.OperatiosService;

@RestController
@RequestMapping(value = "/salesforce")
public class EntryController {

	@Autowired
	ConnectService connectService;

	@Autowired
	OperatiosService showService;

	private String clientId = "3MVG9d8..z.hDcPINdzSLc.18_81q0lUvUfw0Iac.ZFL.ZtuWLr_wlXVtzKGPDTk.7Az6sf61wbcwCLqsrDIU";
	private String clientSecret = "4436621224688664918";
	// private String
	// redirectUri="http://localhost:8080/force_rest_example/oauth/callback";
	private String redirectUri = "http://localhost:8080/retail_salesforce/salesforce/callback";
	private String environment = "https://login.salesforce.com";

	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";

	private String authUrl;
	private String tokenUrl;
	private String accessToken;
	private String instanceUrl;
	private String operation;
	private Map<String,Object> paramMap=new HashMap<String, Object>();

	@RequestMapping(value = "/connect", method = RequestMethod.GET)
	public void connect(HttpServletRequest request, HttpServletResponse response) throws IOException {

		System.out.println("in connect");
		connectService.getConnectInfo2Salesforce(request, response);

		System.out.println("ACCESS_TOKEN  " + request.getSession().getAttribute("ACCESS_TOKEN"));
		System.out.println("INSTANCE_URL  " + request.getSession().getAttribute("INSTANCE_URL"));

	}

	@RequestMapping(value = "/callback", method = RequestMethod.GET)
	public void connect2ndLeg(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// connectService.connect2ndLeg(request,response);

		System.out.println("in test..........");

		tokenUrl = environment + "/services/oauth2/token";

		System.out.println("Auth successful - got callback");

		String code = request.getParameter("code");

		System.out.println("code " + code);

		// Create an instance of HttpClient.
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			// Create an instance of HttpPost.
			HttpPost httpost = new HttpPost(tokenUrl);

			// Adding all form parameters in a List of type NameValuePair

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

				accessToken = authResponse.getString("access_token");
				instanceUrl = authResponse.getString("instance_url");

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
		response.sendRedirect(
				"/retail_salesforce/salesforce/operation?token=" + accessToken + "&instanceUrl=" + instanceUrl + "");

		

	}

	@RequestMapping(value = "/operation/{op}/{mytable}", method = RequestMethod.GET)
	public void operation(HttpServletRequest request, HttpServletResponse response,@PathVariable String op,@PathVariable String mytable)
			throws IOException, ServletException {

		operation=op;
		
		paramMap.put("table", mytable);

		System.out.println("operation  " + operation);
		if (accessToken == null) {
			connect(request, response);
		}

	}

	@RequestMapping(value = "/operation", method = RequestMethod.GET)
	public void allOperation(HttpServletRequest request, HttpServletResponse response, @RequestParam String token,
			@RequestParam String instanceUrl) throws IOException, ServletException {

		PrintWriter writer = response.getWriter();

		System.out.println("token   -->" + token);
		System.out.println("instanceUrl   " + instanceUrl);
		System.out.println("table   " + paramMap.get("table"));

		showService.operations(instanceUrl, accessToken, writer, operation,paramMap);

	}

}
