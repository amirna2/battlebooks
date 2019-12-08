package com.example.battlebooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.battlebooks.model.user.User;
import com.example.battlebooks.repository.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	public UserService() {
		
	}

	public Flux<User> getAllUsers() {
		return userRepo.findAll();
	}
	public Flux<User> getUsersByTeamName(String teamName) {
		return userRepo.findByTeamName(teamName);
	}
	
	public Mono<User> getUserById(String id) {
		return userRepo.findById(id);
	}
	
	public Mono<User> getUserByUsername(String username) {
		return userRepo.findByUserName(username);
	}
	
	public Mono<Void> deleteUserById(String id) {
		return userRepo.deleteById(id);
	}
	
	public Mono<User> saveUser(User book) {
		return userRepo.save(book);
	}
	
	public Mono<Boolean> userExists(String id) {
		return userRepo.existsById(id);
	}
}
