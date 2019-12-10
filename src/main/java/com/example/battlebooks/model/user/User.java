package com.example.battlebooks.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="users")
public class User {

	public static final String KEY_USER_ID = "id";
	@Id private String id;
	@Indexed private String username;
	
    private String password;
    private String role;
    private String fullname;
    public static final String KEY_TEAM = "team";
    @TextIndexed private String team;
    
	public User() {}
	
	public User(String id, String username, String password, String role, String fullname, String team) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.fullname = fullname;
		this.team = team;
	}

	public String getId() {
		return id;
	}

	public User setId(String id) {
		this.id = id;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public User setUsername(String username) {
		this.username = username;
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

	public String getFullname() {
		return fullname;
	}

	public User setFullname(String fullname) {
		this.fullname = fullname;
		return this;
	}

	public String getTeam() {
		return team;
	}

	public User setTeam(String team) {
		this.team = team;
		return this;
	}

	@Override
	public String toString() {
		return String.format("User [id=%s, username=%s, password=%s, role=%s, fullname=%s, team=%s]", id, username,
				password, role, fullname, team);
	}	
}
