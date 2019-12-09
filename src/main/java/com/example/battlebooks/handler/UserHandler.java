package com.example.battlebooks.handler;

import static reactor.core.publisher.Mono.error;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.example.battlebooks.model.user.User;
import com.example.battlebooks.service.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

	final Logger logger = LogManager.getLogger(UserHandler.class.getSimpleName());

	@Autowired
	UserService userService;
	
	public Mono<ServerResponse> getAllUsers(ServerRequest request) {
    	logger.info("getAllUsers: request {}",request.toString());
    	
    	Flux<User> users = userService.getAllUsers();	
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(users, User.class);	
    }

	public Mono<ServerResponse> getUserById(ServerRequest request) {
		String id = request.pathVariable("id");
        
        Mono<User> foundUser = userService.getUserById(id);
        
        return foundUser.flatMap(user -> ServerResponse.ok()
                        	.contentType(MediaType.APPLICATION_JSON)
                        	.body(BodyInserters.fromValue(user)))
                    	.switchIfEmpty(HandlerUtils.errorNotFound("User not found id:"+id));
        }

	public Mono<ServerResponse> deleteUser(ServerRequest request) {
		String id = request.pathVariable("id");        

		return userService.getUserById(id)
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "User does not exist") ))
			.flatMap(user -> {
				return userService.deleteUserById(id).then(Mono.just(user));
			})
			.flatMap(deletedUser -> HandlerUtils.noContent);
	}

	public Mono<ServerResponse> updateUser(ServerRequest request) {
		String id = request.pathVariable("id");	    
	    final String notFoundReason = "User ID "+id+" does not exist";
	    
	    Mono<Boolean> exists = userService.userExists(id);
	    
	    return exists.
	    		flatMap(userExists -> {
	    			if( !userExists) {
	    				return HandlerUtils.errorNotFound(notFoundReason);
	    			}
		    		return request.bodyToMono(User.class)
		    			.flatMap( update -> userService.saveUser(update))
		    			.flatMap(saved -> ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(BodyInserters.fromValue(saved)))
		    			.switchIfEmpty(HandlerUtils.errorServerError("Unable to save updated user"));
	    	});
	}
}
