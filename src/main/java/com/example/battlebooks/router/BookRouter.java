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

import com.example.battlebooks.handler.BookHandler;
import com.example.battlebooks.handler.HandlerUtils;


@Configuration
public class BookRouter {

    @Bean
    public RouterFunction<ServerResponse> bookApiRoute(BookHandler handler){
        RouterFunction<ServerResponse> rf = RouterFunctions
                .route(GET(HandlerUtils.API_BOOKS).and(accept(MediaType.APPLICATION_JSON))
                ,handler::getAllBooks)
                .andRoute(GET(HandlerUtils.API_BOOKS + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,handler::getBookById)
                .andRoute(POST(HandlerUtils.API_BOOKS).and(accept(MediaType.APPLICATION_JSON))
                ,handler::createBook)
                .andRoute(DELETE(HandlerUtils.API_BOOKS + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,handler::deleteBook)
                .andRoute(PUT(HandlerUtils.API_BOOKS + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,handler::updateBook);
        
        return rf;
    }
 
}