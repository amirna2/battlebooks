package com.example.battlebooks.hanlder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.battlebooks.handler.HandlerUtils;
import com.example.battlebooks.model.Book;
import com.example.battlebooks.model.user.User;
import com.example.battlebooks.repository.UserRepository;
import com.example.battlebooks.security.Role;
import com.example.battlebooks.security.TokenProvider;

import reactor.core.publisher.Flux;


@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
public class TestUserHandler {
    final Logger logger = LogManager.getLogger(TestAuthHandler.class.getSimpleName());

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserRepository userRepo;

    @Autowired
    TokenProvider tokenProvider;
    
    private String token;
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
        
        authenticate("amirnathoo","password");
    }
    
    private void authenticate (String username, String password)
    {
       
       Set<GrantedAuthority> roles = new HashSet<> ();
       roles.add (new SimpleGrantedAuthority (Role.ROLE_ADMIN.getAuthority()));

       Authentication fromUsernamePassword = new UsernamePasswordAuthenticationToken (username, password, roles);
       //SecurityContextHolder.getContext ().setAuthentication (auth);
       token = "Bearer " + tokenProvider.generateToken(fromUsernamePassword);
       logger.info ("{} roles:{} ",username, fromUsernamePassword.getAuthorities ());
    }

    
    @Test
    public void testGetAllUsers() {
        webTestClient.get()
        .uri(HandlerUtils.API_USERS)
        .header("Authorization", token)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Book.class)
        .hasSize(users.size());
    }
}
