package com.example.battlebooks.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.battlebooks.handler.HandlerUtils;
import com.example.battlebooks.handler.UserHandler;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userApiRoute(UserHandler handler){
        RouterFunction<ServerResponse> rf = RouterFunctions
        		.route(POST(HandlerUtils.API_USERS).and(accept(MediaType.APPLICATION_JSON)), handler::createUser)
        	    .andRoute(GET(HandlerUtils.API_USERS).and(accept(MediaType.APPLICATION_JSON)), handler::getAllUsers)
        	    .andRoute(GET(HandlerUtils.API_USERS + "/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::getUserById)
        	    .andRoute(PUT(HandlerUtils.API_USERS + "/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::updateUser)
        	    .andRoute(DELETE(HandlerUtils.API_USERS + "/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::deleteUser);
        return rf;
    }    
}