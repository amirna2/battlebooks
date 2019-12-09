package com.example.battlebooks.hanlder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import com.example.battlebooks.handler.HandlerUtils;
import com.example.battlebooks.model.Book;
import com.example.battlebooks.model.user.LoginRequest;
import com.example.battlebooks.model.user.LoginResponse;
import com.example.battlebooks.model.user.User;
import com.example.battlebooks.repository.UserRepository;
import com.example.battlebooks.security.Role;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
public class TestAuthHandler {

	final Logger logger = LogManager.getLogger(TestAuthHandler.class.getSimpleName());

	@Autowired
    WebTestClient webTestClient;
	
    @Autowired
    UserRepository userRepo;
 
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	List<User> users = Arrays.asList(
			new User("0001", "amirnathoo", passwordEncoder.encode("password"), Role.ROLE_ADMIN.name(), "Amir Nathoo", "Team Blue"),
			new User("0002", "cecenathoo", passwordEncoder.encode("password"), Role.ROLE_USER.name(), "Cece Nathoo", "Team Blue"),
			new User("0003", "hugonathoo", passwordEncoder.encode("password"), Role.ROLE_USER.name(), "Hugo Nathoo", "Team Woof"));
	
	
	@AfterAll 
    public void cleanup() {
		userRepo.deleteAll();
    }
    
    @BeforeAll
    public void setupTest() {
    	userRepo.deleteAll()
            .thenMany(Flux.fromIterable(users))
            .flatMap(userRepo::save)
            .doOnNext(user -> {
                System.out.println("[setupTest] Saved User: "+ user);
            })
            .blockLast();  // blocking only for testing purposes
    }

    @Test
    public void testLogin_whenValidCredentials_thenReturnStatusOk() {
    	
    	LoginRequest request = new LoginRequest("amirnathoo","password");
    	 
    	 LoginResponse loggedIn = webTestClient.post()
            .uri(HandlerUtils.API_AUTH.concat("/signin"))
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(request), LoginRequest.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(LoginResponse.class)
            .returnResult()
            .getResponseBody();
    	 
    	 assertTrue(loggedIn.getStatus() == 200);    
    }

}
