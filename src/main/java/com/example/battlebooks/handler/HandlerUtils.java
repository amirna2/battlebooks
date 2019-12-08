package com.example.battlebooks.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public class HandlerUtils {
	public static final String API_QUIZZES = "/api/quizzes";
	public static final String API_CARDS = "/api/cards";
	public static final String API_BOOKS = "/api/books";
	public static final String API_USERS = "/api/users";
	public static final String API_AUTH = "/api/auth";

	static final Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	static final Mono<ServerResponse> badRequest = ServerResponse.badRequest().build();
	static final Mono<ServerResponse> notAllowed = ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	static final Mono<ServerResponse> noContent = ServerResponse.noContent().build();
	static final Mono<ServerResponse> serverError = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	static final Mono<ServerResponse> notImplemented = ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
	

}
