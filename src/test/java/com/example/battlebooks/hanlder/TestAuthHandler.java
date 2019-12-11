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

import com.example.battlebooks.handler.HandlerUtils;
import com.example.battlebooks.model.user.LoginRequest;
import com.example.battlebooks.model.user.LoginResponse;
import com.example.battlebooks.model.user.User;
import com.example.battlebooks.repository.UserRepository;
import com.example.battlebooks.security.Role;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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
            new User("0001", "amirnathoo", passwordEncoder.encode("password"), Role.ROLE_ADMIN.getAuthority(), "Amir Nathoo", "Team Blue"),
            new User("0002", "cecenathoo", passwordEncoder.encode("password"), Role.ROLE_STUDENT.getAuthority(), "Cece Nathoo", "Team Blue"),
            new User("0003", "hugonathoo", passwordEncoder.encode("password"), Role.ROLE_STUDENT.getAuthority(), "Hugo Nathoo", "Team Woof"));


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
	public void testSignIn_whenValidCredentials_thenReturnStatusOk() {

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

		assertTrue(loggedIn.getStatus() == 200 && !loggedIn.getToken().isEmpty() && loggedIn.getUsername().equals("amirnathoo"));    
	}

	@Test
	public void testSignIn_whenInvalidCredentials_thenReturnStatusForbidden() {

		LoginRequest request = new LoginRequest("amirnathoo","notavalidpassword");

		webTestClient.post()
		.uri(HandlerUtils.API_AUTH.concat("/signin"))
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(request), LoginRequest.class)
		.exchange()
		.expectStatus().isForbidden()
		.expectBody(LoginResponse.class)
		.returnResult()
		.getResponseBody();    
	}

	@Test
	public void testSignUp_whenValidUser_thenReturnStatusCreated() {
		User user = new User("0004", "beanathoo", "password", Role.ROLE_STUDENT.getAuthority(), "Bea Nathoo", "Team Purrr");

		User created = webTestClient.post()
				.uri(HandlerUtils.API_AUTH.concat("/signup"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(user), User.class)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(User.class)
				.returnResult()
				.getResponseBody();

		assertTrue(created.getUsername().equals(user.getUsername()));    
	}

	@Test
	public void testSignUp_whenExistingUser_thenReturnStatusBadRequest() {
		User user = new User("0001", "amirnathoo", passwordEncoder.encode("password"), Role.ROLE_ADMIN.name(), "Amir Nathoo", "Team Blue");

		webTestClient.post()
		.uri(HandlerUtils.API_AUTH.concat("/signup"))
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(user), User.class)
		.exchange()
		.expectStatus().isBadRequest()
		.expectBody(User.class)
		.returnResult()
		.getResponseBody();    
	}

}
