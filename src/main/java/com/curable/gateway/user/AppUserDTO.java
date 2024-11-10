package com.curable.gateway.user;

/**
 * 
 * This is the app user for the application.
 *
 */
public class AppUserDTO {

	private Long id;

	private String userName;

	private String password;

	private String role;

	private String token;

	private double apiVersion;

	

	public AppUserDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public AppUserDTO(String userName, String password, String role) {
		super();
		this.userName = userName;
		this.password = password;
		this.role = role;
	}



	public AppUserDTO(Long id, String userName, String password, String role, String token, double apiVersion) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.role = role;
		this.token = token;
		this.apiVersion = apiVersion;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getUserName() {
		return userName;
	}



	public void setUserName(String userName) {
		this.userName = userName;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getRole() {
		return role;
	}



	public void setRole(String role) {
		this.role = role;
	}



	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}



	public double getApiVersion() {
		return apiVersion;
	}



	public void setApiVersion(double apiVersion) {
		this.apiVersion = apiVersion;
	}



	@Override
	public String toString() {
		return "AppUserDTO [userName=" + userName + ", role=" + role + ", token=" + token + ", apiVersion=" + apiVersion
				+ "]";
	}

	

}
