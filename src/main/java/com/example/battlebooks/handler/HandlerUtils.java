package com.example.battlebooks.handler;

import static reactor.core.publisher.Mono.error;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

public class HandlerUtils {
	public static final String API_QUIZZES = "/api/quizzes";
	public static final String API_CARDS = "/api/cards";
	public static final String API_BOOKS = "/api/books";
	public static final String API_USERS = "/api/users";
	public static final String API_AUTH = "/api/auth";

	static final Mono<ServerResponse> notImplemented = ServerResponse.status(HttpStatus.NOT_IMPLEMENTED).build();
	static final Mono<ServerResponse> noContent = ServerResponse.noContent().build();
	
	
	static final Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	static final Mono<ServerResponse> badRequest = ServerResponse.badRequest().build();
	static final Mono<ServerResponse> notAllowed = ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	static final Mono<ServerResponse> serverError = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	
	static Mono<ServerResponse> errorNotFound(@Nullable String reason) {
		return error(new ResponseStatusException(HttpStatus.NOT_FOUND, reason));
	}
	
	static Mono<ServerResponse> errorBadRequest(@Nullable String reason) {
		return error(new ResponseStatusException(HttpStatus.BAD_REQUEST, reason));
	}
	static Mono<ServerResponse> errorServerError(@Nullable String reason) {
		return error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason));
	}
	static Mono<ServerResponse> errorAccessDenied(@Nullable String reason) {
		return error(new ResponseStatusException(HttpStatus.FORBIDDEN, reason));
	}
	
	static Mono<ServerResponse> errorNotAllowed(@Nullable String reason) {
		return error(new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, reason));
	}
}
