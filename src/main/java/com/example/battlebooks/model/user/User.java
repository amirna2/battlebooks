package com.example.battlebooks.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="users")
public class User {

	public static final String KEY_USER_ID = "id";
	@Id private String id;
	@Indexed private String userName;
	
    private String password;
    private String role;
    private String fullName;
    public static final String KEY_TEAM_NAME = "teamName";
    @TextIndexed private String teamName;
    
	public User() {}
	
	public User(String id, String userName, String password, String role, String fullName, String teamName) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.role = role;
		this.fullName = fullName;
		this.teamName = teamName;
	}

	public String getId() {
		return id;
	}

	public User setId(String id) {
		this.id = id;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public User setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getRole() {
		return role;
	}

	public User setRole(String role) {
		this.role = role;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public User setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public String getTeamName() {
		return teamName;
	}

	public User setTeamName(String teamName) {
		this.teamName = teamName;
		return this;
	}

	@Override
	public String toString() {
		return String.format("User [id=%s, userName=%s, password=%s, role=%s, fullName=%s, teamName=%s]", id, userName,
				password, role, fullName, teamName);
	}	
}
