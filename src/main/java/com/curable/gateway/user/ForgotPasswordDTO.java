package com.curable.gateway.user;

public class ForgotPasswordDTO {
	
	private String message;
	private int status;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ForgotPasswordDTO [message=" + message + ", status=" + status + "]";
	}
	public ForgotPasswordDTO(String message, int status) {
		super();
		this.message = message;
		this.status = status;
	}
	public ForgotPasswordDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
