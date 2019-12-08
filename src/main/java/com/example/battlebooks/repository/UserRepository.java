package com.example.battlebooks.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.example.battlebooks.model.user.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

	Mono<User> findByUserName(String string);
	Flux<User> findByTeamName(String string);
	
}