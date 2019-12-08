package com.example.battlebooks.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class UserHandler {

	 public Mono<ServerResponse> getAllUsers(ServerRequest request) {
		 return HandlerUtils.notImplemented;
	 }
	 
	 public Mono<ServerResponse> getUserById(ServerRequest request) {
		 return HandlerUtils.notImplemented;
	 }
	 
	 public Mono<ServerResponse> createUser(ServerRequest request) {
		 return HandlerUtils.notImplemented;
	 }
	 
	 public Mono<ServerResponse> deleteUser(ServerRequest request) {
		 return HandlerUtils.notImplemented;
	 }
	 
	 public Mono<ServerResponse> updateUser(ServerRequest request) {
		 return HandlerUtils.notImplemented;
	 }
}
