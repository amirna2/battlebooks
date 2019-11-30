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
import com.example.battlebooks.handler.QuizzHandler;

@Configuration
public class QuizzRouter {
	@Bean
	public RouterFunction<ServerResponse> quizzApiRoute(QuizzHandler handler) {
		RouterFunction<ServerResponse> rf = RouterFunctions
				.route(GET(HandlerUtils.API_QUIZZES).and(accept(MediaType.APPLICATION_JSON)),
						handler::getAllQuizzes)
				.andRoute(GET(HandlerUtils.API_QUIZZES + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
						handler::getQuizzById)
				.andRoute(POST(HandlerUtils.API_QUIZZES).and(accept(MediaType.APPLICATION_JSON)),
						handler::createQuizz)
				.andRoute(DELETE(HandlerUtils.API_QUIZZES + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
						handler::deleteQuizz)
				.andRoute(PUT(HandlerUtils.API_QUIZZES + "/{id}").and(accept(MediaType.APPLICATION_JSON)),
						handler::updateQuizz);

		return rf;
	}
}
