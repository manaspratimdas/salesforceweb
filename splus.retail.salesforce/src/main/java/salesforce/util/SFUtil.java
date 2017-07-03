package salesforce.util;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

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
}
