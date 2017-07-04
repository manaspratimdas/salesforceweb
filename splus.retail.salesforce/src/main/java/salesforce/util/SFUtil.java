package salesforce.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import salesforce.entity.ColumnValuePair;
import salesforce.entity.UpdateParam;

@Component
public class SFUtil {

	public String getRefreshToken() {

		System.out.println("read file");
		String refreshToken = "";
		ClassLoader classLoader = getClass().getClassLoader();

		try {
			refreshToken = IOUtils.toString(classLoader.getResourceAsStream("/token.txt"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return refreshToken;

	}

	public Map<String, Object> getParamMapForCreateByName(InputStream myjson) throws IOException {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String inputJson = IOUtils.toString(myjson, "utf-8");
		JSONObject object = new JSONObject(inputJson);
		
		String table = object.getString("table");
		String name = object.getString("name");

		paramMap.put("table", table);
		paramMap.put("name", name);

		return paramMap;
	}

	public Map<String, Object> getParamMapForEntity(InputStream myjson) throws IOException {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String inputJson = IOUtils.toString(myjson, "utf-8");
		JSONObject object = new JSONObject(inputJson);
		
		String table = object.getString("table");
		

		paramMap.put("table", table);
		

		return paramMap;
	}

	public UpdateParam getParamMapForUpdateById(InputStream myjson) throws IOException {
		
		UpdateParam updateParam=new UpdateParam();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String inputJson = IOUtils.toString(myjson, "utf-8");
		JSONObject object = new JSONObject(inputJson);
		
		
		String paramColumnValuePair = object
				.getString("ColumnValuePair");
		Gson gson = new Gson();
		ColumnValuePair columnValuePair = gson.fromJson(paramColumnValuePair, ColumnValuePair.class);
		
		System.out.println("ColumnValuePair "+columnValuePair);
		
		String table= object.getString("table");
		
		
		
		
		
		
		return null;
	}
}
