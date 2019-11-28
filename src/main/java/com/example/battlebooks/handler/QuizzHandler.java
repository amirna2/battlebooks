package com.example.battlebooks.handler;

import static reactor.core.publisher.Mono.error;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.example.battlebooks.model.Quizz;
import com.example.battlebooks.service.QuizzService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class QuizzHandler {
	public static final String  API_CARDS = "/api/quizz";

    final Logger logger = LogManager.getLogger(QuizzHandler.class.getSimpleName());

	static final Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	static final Mono<ServerResponse> badRequest = ServerResponse.badRequest().build();
	static final Mono<ServerResponse> notAllowed = ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	static final Mono<ServerResponse> noContent = ServerResponse.noContent().build();
	static final Mono<ServerResponse> serverError = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	@Autowired
	QuizzService quizzService;
	
    public Mono<ServerResponse> getAllQuizzes(ServerRequest request) {
    	
    	logger.info("getAllQuizzes: request {}",request.toString());
    	
    	Flux<Quizz> quizzes = quizzService.getAllQuizzes();
    	
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(quizzes, Quizz.class);
    }

    public Mono<ServerResponse> getQuizzById(ServerRequest request) {
        String id = request.pathVariable("id");
        
        Mono<Quizz> foundQuizz = quizzService.getQuizzById(id);
        
        return foundQuizz.flatMap(book -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(book)))
                    .switchIfEmpty(notFound);
    }
        
    public Mono<ServerResponse> createBook(ServerRequest request) {
        Mono<Quizz> quizzToCreate = request.bodyToMono(Quizz.class);
        
        return quizzToCreate.flatMap(book -> 
            ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .body(quizzService.saveQuizz(book),Quizz.class));
    }
   
    public Mono<ServerResponse> deleteQuizz(ServerRequest request) {
    	String id = request.pathVariable("id");        

		return quizzService.getQuizzById(id)
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED)))
			.flatMap(quizz -> {
				return quizzService.deleteQuizzById(id).then(Mono.just(quizz));
			})
			.flatMap(deletedQuizz -> noContent);
	}
    
    public Mono<ServerResponse> deleteQuizz2(ServerRequest request) {
    	String id = request.pathVariable("id");        

	    final String serverErrorReason = "Failed to delete quizz by ID: "+id;

		return quizzService.getQuizzById(id)
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED)))
			.flatMap(quizz -> {
				return quizzService.deleteQuizzById(id)
					    .doOnError(t -> logger.warn("QuizzHandler: Failed to delete Quizz {} - {}", id, t.getMessage() ))
						.onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,serverErrorReason));
			})
			.flatMap(deletedQuizz -> noContent);
	}
    
    public Mono<ServerResponse> updateQuizz(ServerRequest request) {
	    String id = request.pathVariable("id");	    
	    final String notFoundReason = "Quizz ID "+id+" does not exist";
	    
	    Mono<Boolean> exists = quizzService.quizzExists(id);

	    return exists.
	    		flatMap(quizzExists -> {
	    			if( !quizzExists) {
	    				return error(new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundReason));
	    			}
		    		return request.bodyToMono(Quizz.class)
		    			.flatMap( update -> quizzService.saveQuizz(update))
		    			.flatMap(saved -> ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(BodyInserters.fromValue(saved)))
		    			.switchIfEmpty(serverError);
	    	});
    } 	
}
