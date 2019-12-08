package com.example.battlebooks.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class AuthHandler {
	
	public Mono<ServerResponse> login(ServerRequest request) {
        return null;
    }
	
	public Mono<ServerResponse> signUp(ServerRequest request) {
        return null;
    }
	public Mono<ServerResponse> signOut(ServerRequest request) {
        return null;
    }
}
