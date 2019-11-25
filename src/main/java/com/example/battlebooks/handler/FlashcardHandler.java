package com.example.battlebooks.handler;

import static reactor.core.publisher.Mono.error;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.example.battlebooks.model.Flashcard;
import com.example.battlebooks.model.QuestionType;
import com.example.battlebooks.service.FlashcardService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FlashcardHandler {

	public static final String  API_CARDS = "/api/cards";

	static final Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	static final Mono<ServerResponse> badRequest = ServerResponse.badRequest().build();
	static final Mono<ServerResponse> notAllowed = ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	static final Mono<ServerResponse> noContent = ServerResponse.noContent().build();
	static final Mono<ServerResponse> serverError = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	@Autowired
	FlashcardService cardService;
	
	
    public Mono<ServerResponse> getCardById(ServerRequest request) {
        String id = request.pathVariable("id");
        
        Mono<Flashcard> found = cardService.getCardById(id);
        
        return found.flatMap(card -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(card)))
                    .switchIfEmpty(notFound);
    }
    
    // e.g : /api/cards?type="content"?category="place"?bookTitle="In Frogkisser!"
    //       /api/cards?type="content"?category="character"
    public Mono<ServerResponse> getCardByQuery(ServerRequest request) {   	   
    	   Flux<Flashcard> cards = cardService.getCardsByQuery(request.queryParams());
    	   if (cards == null) {
    		   return badRequest;
    	   }
           return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(cards, Flashcard.class);
    }
    
    public Mono<ServerResponse> createCard(ServerRequest request) {
    	
    	//TODO: check the book title in the card to create actually exists
        Mono<Flashcard> cardToCreate = request.bodyToMono(Flashcard.class);
        return cardToCreate.flatMap(card -> 
            ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cardService.saveCard(card),Flashcard.class));
    }
   
    public Mono<ServerResponse> deleteCard(ServerRequest request) {
    	String id = request.pathVariable("id");        

		return cardService.getCardById(id)
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
			.flatMap(card -> {
				return cardService.deleteCardById(id).then(Mono.just(card));
			})
			.flatMap(deletedCard -> noContent);
	}
        
    public Mono<ServerResponse> updateCard(ServerRequest request) {
	    String id = request.pathVariable("id");
	    
	    final String notFoundReason = "Flashcard "+id+" does not exist";
	    
	    return cardService.getCardById(id)
	    		  .switchIfEmpty(error(new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundReason)))
	    		  .then(request.bodyToMono(Flashcard.class))
	    		  .flatMap( update -> cardService.saveCard(update))
	    		  .flatMap(saved -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(saved)))
				  .switchIfEmpty(serverError);
    }
}
