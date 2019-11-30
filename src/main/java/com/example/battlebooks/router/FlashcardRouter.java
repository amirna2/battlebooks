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

import com.example.battlebooks.handler.FlashcardHandler;
import com.example.battlebooks.handler.HandlerUtils;


@Configuration
public class FlashcardRouter {

    @Bean
    public RouterFunction<ServerResponse> cardApiRoute(FlashcardHandler handler){
        RouterFunction<ServerResponse> rf = RouterFunctions
                .route(GET(HandlerUtils.API_CARDS).and(accept(MediaType.APPLICATION_JSON))
                ,handler::getCardByQuery)
                .andRoute(GET(HandlerUtils.API_CARDS + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,handler::getCardById)
                .andRoute(POST(HandlerUtils.API_CARDS).and(accept(MediaType.APPLICATION_JSON))
                ,handler::createCard)
                .andRoute(DELETE(HandlerUtils.API_CARDS + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,handler::deleteCard)
                .andRoute(PUT(HandlerUtils.API_CARDS + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,handler::updateCard);
        
        return rf;
    }
}