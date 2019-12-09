package com.example.battlebooks.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.battlebooks.model.user.LoginRequest;
import com.example.battlebooks.model.user.LoginResponse;
import com.example.battlebooks.model.user.User;
import com.example.battlebooks.security.TokenProvider;
import com.example.battlebooks.service.UserService;

import reactor.core.publisher.Mono;

@Component
public class AuthHandler {
	
    final Logger logger = LogManager.getLogger(AuthHandler.class.getSimpleName());

    
	@Autowired
	UserService userService;
	
	@Autowired
	TokenProvider tokenProvider;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// /api/auth/signin
	public Mono<ServerResponse> signIn(ServerRequest request) {

		logger.info("REQUEST:/api/auth/signIn  {}",request.toString());

		Mono<LoginRequest> loginRequest = request.bodyToMono(LoginRequest.class).log();
		
		return loginRequest
				.flatMap(login -> userService.getUserByUsername(login.getUsername())
							.flatMap(user -> {
								logger.info("signIn: checking passwords");
								if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
									logger.info("signIn: Password match!!!");

									UsernamePasswordAuthenticationToken fromUsernamePassword = 
											new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
									String token = tokenProvider.generateToken(fromUsernamePassword);
									
									LoginResponse response = new LoginResponse(200, "success", user.getUserName(),token);
									logger.info("signIn: sending response : {}",response.toString());
									
									return ServerResponse.ok()
											.contentType(MediaType.APPLICATION_JSON)
											.body(BodyInserters.fromValue(response));
									
								} else {
									logger.error("signIn: Password incorrect!!!");
									return HandlerUtils.errorAccessDenied("Password incorrect");
								}
				}).switchIfEmpty(HandlerUtils.errorNotFound("User not found")));
	}    
	
	// /api/auth/signup
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
