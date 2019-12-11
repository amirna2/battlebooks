package com.example.battlebooks.hanlder;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.battlebooks.handler.HandlerUtils;
import com.example.battlebooks.model.user.User;


public class TestUserHandler extends TestHandler{
    final Logger logger = LogManager.getLogger(TestAuthHandler.class.getSimpleName());

    @Autowired
    WebTestClient webTestClient;
    
    @Test
    public void testGetAllUsers() {
        webTestClient.get()
        .uri(HandlerUtils.API_USERS)
        .header("Authorization", token)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(User.class)
        .hasSize(users.size());
    }
}
