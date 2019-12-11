package com.example.battlebooks.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.battlebooks.model.user.User;
import com.example.battlebooks.security.Role;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
public class TestUserRepository {

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
    public void findAll() {
        Flux<User> userFlux = userRepo.findAll();
        
        StepVerifier.create(userFlux)
        .expectSubscription()
        .expectNextCount(3)
        .expectComplete();
    }
    
    @Test
    public void findByUserName() {
        Mono<User> userMono = userRepo.findByUsername("cecenathoo");
        
        StepVerifier.create(userMono)
        .expectSubscription()
        .expectNextCount(1)
        .expectComplete();
    }
    
    @Test
    public void findByTeam() {
        Flux<User> userFlux = userRepo.findByTeam("Team Blue");
        
        StepVerifier.create(userFlux)
        .expectSubscription()
        .expectNextCount(2)
        .expectComplete();
    }
    
    
}
