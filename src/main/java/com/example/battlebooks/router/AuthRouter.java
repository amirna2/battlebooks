package com.example.battlebooks.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.battlebooks.handler.AuthHandler;
import com.example.battlebooks.handler.HandlerUtils;

@Configuration
public class AuthRouter {
    
	@Bean
    public RouterFunction<ServerResponse> authApiRoute(AuthHandler handler){
        RouterFunction<ServerResponse> rf = RouterFunctions
        		.route(POST(HandlerUtils.API_AUTH + "/signin").and(accept(MediaType.APPLICATION_JSON)), handler::signIn)
        	    .andRoute(GET(HandlerUtils.API_AUTH + "/signup").and(accept(MediaType.APPLICATION_JSON)), handler::signUp)
        	    .andRoute(GET(HandlerUtils.API_AUTH + "/signout").and(accept(MediaType.APPLICATION_JSON)), handler::signOut);
        return rf;
    } 
}