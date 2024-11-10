package com.curable.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@RefreshScope
public class PropertyConfig {

	/*
	 * keycloak properties
	 */
	private String keyauthServerUrl;
	private String keypublicClient;
	private String keyrealm;
	private String keyresource;

	/*
	 * keycloak-admin properties
	 */
	private String keyadminclientId;
	private String keyadminrealm;
	private String keyadminserverUrl;
	private String keyadminusername;
	private String keyadminpassword;
	private String keyadminuserrealm;

//	static token
	private int staticTokenYears;

	private String staticTokenIntervalBy;

	public String getKeyauthServerUrl() {
		return keyauthServerUrl;
	}

	@Value("${keycloak.auth-server-url}")
	public void setKeyauthServerUrl(String keyauthServerUrl) {
		this.keyauthServerUrl = keyauthServerUrl;
	}

	public String getKeypublicClient() {
		return keypublicClient;
	}

	@Value("${keycloak.public-client}")
	public void setKeypublicClient(String keypublicClient) {
		this.keypublicClient = keypublicClient;
	}

	public String getKeyrealm() {
		return keyrealm;
	}

	@Value("${keycloak.realm}")
	public void setKeyrealm(String keyrealm) {
		this.keyrealm = keyrealm;
	}

	public String getKeyresource() {
		return keyresource;
	}

	@Value("${keycloak.resource}")
	public void setKeyresource(String keyresource) {
		this.keyresource = keyresource;
	}

	public String getKeyadminclientId() {
		return keyadminclientId;
	}

	@Value("${keycloak-admin.clientId}")
	public void setKeyadminclientId(String keyadminclientId) {
		this.keyadminclientId = keyadminclientId;
	}

	public String getKeyadminrealm() {
		return keyadminrealm;
	}

	@Value("${keycloak-admin.realm}")
	public void setKeyadminrealm(String keyadminrealm) {
		this.keyadminrealm = keyadminrealm;
	}

	public String getKeyadminserverUrl() {
		return keyadminserverUrl;
	}

	@Value("${keycloak-admin.serverUrl}")
	public void setKeyadminserverUrl(String keyadminserverUrl) {
		this.keyadminserverUrl = keyadminserverUrl;
	}

	public String getKeyadminusername() {
		return keyadminusername;
	}

	@Value("${keycloak-admin.username}")
	public void setKeyadminusername(String keyadminusername) {
		this.keyadminusername = keyadminusername;
	}

	public String getKeyadminpassword() {
		return keyadminpassword;
	}

	@Value("${keycloak-admin.userrealm}")
	public void setKeyadminpassword(String keyadminpassword) {
		this.keyadminpassword = keyadminpassword;
	}

	public String getKeyadminuserrealm() {
		return keyadminuserrealm;
	}

	@Value("${keycloak-admin.password}")
	public void setKeyadminuserrealm(String keyadminuserrealm) {
		this.keyadminuserrealm = keyadminuserrealm;
	}

	public int getStaticTokenYears() {
		return staticTokenYears;
	}

	@Value("${security.jwt.token.static.years:5}")
	public void setStaticTokenYears(int staticTokenYears) {
		this.staticTokenYears = staticTokenYears;
	}

	public String getStaticTokenIntervalBy() {
		return staticTokenIntervalBy;
	}

	@Value("${security.jwt.token.static.interval:YEARS}")
	public void setStaticTokenIntervalBy(String staticTokenIntervalBy) {
		this.staticTokenIntervalBy = staticTokenIntervalBy;
	}

}
