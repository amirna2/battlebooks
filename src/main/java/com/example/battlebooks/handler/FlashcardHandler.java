package com.example.battlebooks.handler;

import static reactor.core.publisher.Mono.error;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.example.battlebooks.model.Flashcard;
import com.example.battlebooks.model.QuestionType;
import com.example.battlebooks.repository.FlashcardRepository;

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

	private ReactiveMongoTemplate template;
	
	@Autowired
	FlashcardRepository repository;
	
	public FlashcardHandler(ReactiveMongoTemplate template) {
		this.template = template;
	}
	
    private Mono<ServerResponse> getAllCards(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(repository.findAll(), Flashcard.class);
    }

    public Mono<ServerResponse> getCardById(ServerRequest request) {
        String id = request.pathVariable("id");
        
        Mono<Flashcard> found = repository.findById(id);
        
        return found.flatMap(card -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(card)))
                    .switchIfEmpty(notFound);
    }
    
    // e.g : /api/cards?type="content"?category="place"?bookTitle="In Frogkisser!"
    //       /api/cards?type="content"?category="character"
    public Mono<ServerResponse> getCardByQuery(ServerRequest request) {
    	   
    	   if(request.queryParams().isEmpty()) {
    		   return getAllCards(request);
    	   }
    	   Optional<String> type = request.queryParam(Flashcard.KEY_TYPE);
    	   Optional<String> category = request.queryParam(Flashcard.KEY_CATEGORY);
    	   Optional<String> book = request.queryParam(Flashcard.KEY_BOOK);
    	   
    	   // There is no category when the question is "In which book...."
    	   // This should be checked from the front end, but we'll return a 400 status in any case.
    	   if(type.isPresent() && type.get().equals(QuestionType.IN_WHICH_BOOK) && category.isPresent()) {
    		   return ServerResponse.badRequest().body(BodyInserters.fromValue("category not supported for given question type"));
    	   }
    	   
    	   // We have some query parameters, let's build query and find some results
    	   Query query = new Query();
    	   
    	   // we can have comma separated strings for each query param. e.g type="content,author"
    	   type.ifPresent(param -> {
    		   List<String> paramList = Arrays.asList(param.split("\\s*,\\s*"));
    		      query.addCriteria(Criteria.where(Flashcard.KEY_TYPE).in(paramList));
    		   
    	   });
    	   category.ifPresent(param -> {
    		   List<String> paramList = Arrays.asList(param.split("\\s*,\\s*"));
    			   query.addCriteria(Criteria.where(Flashcard.KEY_CATEGORY).in(paramList));

    	   });
    	   book.ifPresent(param -> {
    		   List<String> paramList = Arrays.asList(param.split("\\s*,\\s*"));
    			   query.addCriteria(Criteria.where(Flashcard.KEY_BOOK).in(paramList));
    	   });
    	   
    	   // example query:
    	   // Query: { "type" : { "$in" : ["content"]}, "category" : { "$in" : ["object", "date"]}}, Fields: {}, Sort: {}
    	   Flux<Flashcard> cards = template.find(query, Flashcard.class);
    	   
           return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(cards, Flashcard.class);
    }
    
    public Mono<ServerResponse> createCard(ServerRequest request) {
        Mono<Flashcard> cardToCreate = request.bodyToMono(Flashcard.class);
        return cardToCreate.flatMap(item -> 
            ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .body(repository.save(item),Flashcard.class));
    }
   
    public Mono<ServerResponse> deleteCard(ServerRequest request) {
    	String id = request.pathVariable("id");        

		return repository.findById(id)
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
			.flatMap(card -> {
				return repository.deleteById(id).then(Mono.just(card));
			})
			.flatMap((deletedCard) -> { return noContent;});
	}
        
    public Mono<ServerResponse> updateCard(ServerRequest request) {
	    String id = request.pathVariable("id");
	    
	    final String notFoundReason = "Flashcard "+id+" does not exist";
	    
	    /*
	     * find the card, return not found if card doesn't exist
	     * or update (save) the requested card and return it if saved
	     * otherwise, return a server error
	     */
	    return repository
	    		.findById(id).log("Found Card: ")
				.switchIfEmpty(error(new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundReason)))
				.or(request.bodyToMono(Flashcard.class))
	    		.flatMap(cardToUpdate -> repository.save(cardToUpdate))
			    .flatMap(savedCard -> 
			    			ServerResponse.ok()
			    			.contentType(MediaType.APPLICATION_JSON)
			    			.body(BodyInserters.fromValue(savedCard)))
			    .switchIfEmpty(serverError);
	}
}
