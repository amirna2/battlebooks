package com.example.battlebooks.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="users")
public class User {

	public static final String KEY_USER_ID = "id";
	@Id private String id;
	
	private String userName;
    private String password;
    private String role;
    private String fullName;
    public static final String KEY_TEAM_NAME = "teamName";
    @TextIndexed private String teamName;
}
