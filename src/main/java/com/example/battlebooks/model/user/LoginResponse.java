package com.example.battlebooks.model.user;

public class LoginResponse {

    private int status;
    private String message;
    private String username;
    private String token;

    public LoginResponse() {}
    
    public LoginResponse(int status, String message, String username, String token) {
		this.status = status;
		this.message = message;
		this.username = username;
		this.token = token;
	}

	public int getStatus() {
        return status;
    }

    public LoginResponse setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LoginResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public LoginResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

	@Override
	public String toString() {
		return "LoginResponse [status=" + status + ", message=" + message + ", username=" + username + ", token="
				+ token + "]";
	}
    
}