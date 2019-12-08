package com.example.battlebooks.handler;

import static reactor.core.publisher.Mono.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.example.battlebooks.model.Book;
import com.example.battlebooks.model.Flashcard;
import com.example.battlebooks.model.user.LoginRequest;
import com.example.battlebooks.model.user.LoginResponse;
import com.example.battlebooks.model.user.User;
import com.example.battlebooks.repository.UserRepository;
import com.example.battlebooks.security.TokenProvider;
import com.example.battlebooks.service.UserService;

import reactor.core.publisher.Mono;

@Component
public class AuthHandler {
	
	@Autowired
	UserService userService;
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	TokenProvider tokenProvider;

	public Mono<ServerResponse> login(ServerRequest request) {

		Mono<LoginRequest> loginRequest = request.bodyToMono(LoginRequest.class);
		return loginRequest.flatMap(login -> userService.getUserByUsername(login.getUsername()).flatMap(user -> {
			if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
				UsernamePasswordAuthenticationToken fromUsernamePassword = new UsernamePasswordAuthenticationToken(
						login.getUsername(), login.getPassword());
				LoginResponse response = new LoginResponse(200, "success", user.getUserName(),
						tokenProvider.generateToken(fromUsernamePassword));
				return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(response, LoginResponse.class);
			} else {
				return HandlerUtils.errorAccessDenied("Password incorrect");
			}
		}).switchIfEmpty(HandlerUtils.errorNotFound("Username not found")));
	}    
	
	public Mono<ServerResponse> signUp(ServerRequest request) {
		Mono<User> userMono = request.bodyToMono(User.class);

		// prepare our new user by encrypting the given password
		Mono<User> newUser = userMono.map(user -> {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			return user;
		});
		// return the saved user if it didn't exist already, otherwise send an error response
		return newUser.flatMap(user -> userService.getUserByUsername(user.getUserName())
				.flatMap(foundUser -> HandlerUtils.errorBadRequest("This username already exists") 
				.switchIfEmpty(ServerResponse.created(null)
						.contentType(MediaType.APPLICATION_JSON)
						.body(userService.saveUser(user),User.class))));

	}

	public Mono<ServerResponse> signOut(ServerRequest request) {
        return HandlerUtils.notImplemented;
    }
}
