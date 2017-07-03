package salesforce.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import salesforce.service.ConnectService;
import salesforce.service.SFService;
import salesforce.util.SFUtil;

@RestController
@RequestMapping(value = "/salesforce")
public class SFController {

	@Autowired
	ConnectService connectService;

	@Autowired
	SFService   sFService;

	@Autowired
	SFUtil sfutil;

	private String clientId = "3MVG9d8..z.hDcPINdzSLc.18_81q0lUvUfw0Iac.ZFL.ZtuWLr_wlXVtzKGPDTk.7Az6sf61wbcwCLqsrDIU";
	private String clientSecret = "4436621224688664918";

	private String redirectUri = "http://localhost:8080/retail_salesforce/salesforce/callback";
	private String environment = "https://login.salesforce.com";

	private String authUrl;
	private String tokenUrl;
	private String accessToken;
	private String instanceUrl;
	private String operation;
	private Map<String, Object> paramMap = new HashMap<String, Object>();

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

				System.out.println("rstream.toString()--->" + rstream.toString());
				System.out.println("authResponse--->" + authResponse);

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

	@SuppressWarnings("unused")
	@RequestMapping(value = "/display", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public String display(HttpServletRequest request, HttpServletResponse response, InputStream myjson)
			throws IOException, ServletException {
		System.out.println("in post " + myjson);

		String inputJson = IOUtils.toString(myjson, "utf-8");
		
		
		String resultJson = "Refresh token not yet generated. Generate token with below url \\n https://localhost:8443/retail_salesforce/salesforce";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		

		JSONObject object = new JSONObject(inputJson);
		String table = object.getString("table");
		
		
		paramMap.put("table", table);	
		
		String refreshToken = sfutil.getRefreshToken();
		Map<String, String> connectParam = new HashMap<String, String>();
		if (refreshToken != null || refreshToken != "") {

			connectParam = connectService.connectWithRefreshToken(refreshToken);

		} 

		resultJson = sFService.display(connectParam.get("INSTANCE_URL"), connectParam.get("ACCESS_TOKEN"), paramMap);

		//System.out.println("theString " + resultJson);

		return resultJson;

	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public String delete(HttpServletRequest request, HttpServletResponse response, InputStream myjson)
			throws IOException, ServletException {

		System.out.println("in post " + myjson);

		String inputJson = IOUtils.toString(myjson, "utf-8");
		
		
		String resultJson = "Refresh token not yet generated. Generate token with below url \\n https://localhost:8443/retail_salesforce/salesforce/connect";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		

		JSONObject object = new JSONObject(inputJson);
		String table = object.getString("table");
		String id = object.getString("id");
		
		
		paramMap.put("table", table);
		paramMap.put("id", id);	
		
		String refreshToken = sfutil.getRefreshToken();
		Map<String, String> connectParam = new HashMap<String, String>();
		if (refreshToken != null || refreshToken != "") {

			connectParam = connectService.connectWithRefreshToken(refreshToken);

		} 
		
		
		sFService.delete(connectParam.get("INSTANCE_URL"), connectParam.get("ACCESS_TOKEN"), paramMap);
		
		
		
		return accessToken;

	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	public String update(HttpServletRequest request, HttpServletResponse response, InputStream myjson)
			throws IOException, ServletException {

		return accessToken;

	}

}
