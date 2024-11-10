/**
 * 
 */
package com.curable.gateway.user.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.curable.gateway.user.AppUserDTO;
import com.curable.gateway.util.Constant;

/**
 * This is abstract wrapper class for keycloak server user integrations.
 * @author Annadurai S
 *
 */
public abstract class UserServiceKeycloakWrapper {

	private static final Logger log = LoggerFactory.getLogger(UserServiceKeycloakWrapper.class);

	@Value("${keycloak.resource:secret}")
	private String client_id = "curable-project";

	@Value("${keycloak.realm}")
	private String client_relam = "curable";

	@Value("${keycloak.auth-server-url}")
	private String keycloak_auth_server_url = "http://localhost:8080/auth";

	/**
	 * This method used to build the keycloak server auth url 
	 * dynamically based on the configuration properties.
	 * @return
	 */
	private String buildAuthUrl() {

		StringBuilder authUrl = new StringBuilder();

		authUrl.append(keycloak_auth_server_url);
		authUrl.append("/");
		authUrl.append("realms");
		authUrl.append("/");
		authUrl.append(client_relam);
		authUrl.append("/");
		authUrl.append("protocol/openid-connect/token");
		return authUrl.toString();
	}

	/**
	 * This methods used to check the user exists/not  in keycloak server.
	 * @param userDetails
	 * @return
	 */
	public boolean checkKeycloakUserAvailable(AppUserDTO userDetails) {

		String username = null;
		String password = null;
		int statusCode = -1;
		boolean result = false;

		try {
			String uri = buildAuthUrl();

			/* uri = buildAuthUrl(); */

			log.info("Keycloak Auth url :: " + uri);

			if (userDetails != null) {
				username = userDetails.getUserName();
				password = userDetails.getPassword(); 			}

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(uri);
			post.setHeader("User-Agent", Constant.USER_AGENT); // --TODO check dynamic
			List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
			urlParameters.add(new BasicNameValuePair("grant_type", Constant.GRANT_TYPE));
			urlParameters.add(new BasicNameValuePair("client_id", client_id));
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", password));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(post);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == Constant.STATUS_CODE_200 || statusCode == Constant.STATUS_CODE_201)
				result = true;

		} catch (Exception e) {
			//e.printStackTrace();
			result = false;
		}

		return result;
	}

}
