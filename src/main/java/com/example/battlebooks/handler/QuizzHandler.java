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

    final Logger logger = LogManager.getLogger(QuizzHandler.class.getSimpleName());

	@Autowired
	QuizzService quizzService;
	
    public Mono<ServerResponse> getAllQuizzes(ServerRequest request) {
    	
    	logger.info("getAllQuizzes: request {}",request.toString());
    	
    	Flux<Quizz> quizzes = quizzService.getAllQuizzes();
    	
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(quizzes, Quizz.class);
    }

    public Mono<ServerResponse> getQuizzById(ServerRequest request) {
        String id = request.pathVariable("id");
        
    	logger.info("getQuizzById: request {} - id:{}",request.toString(), id);

        Mono<Quizz> foundQuizz = quizzService.getQuizzById(id);
        
        return foundQuizz.flatMap(quizz -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(quizz)))
                    .switchIfEmpty(HandlerUtils.notFound);
    }
        
    public Mono<ServerResponse> createQuizz(ServerRequest request) {
        Mono<Quizz> quizzToCreate = request.bodyToMono(Quizz.class);

        logger.info("createQuizz:\nREQUEST {}\n{}",request.toString(), quizzToCreate.toProcessor().peek());

        return quizzToCreate.flatMap(quizz -> 
            ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .body(quizzService.saveQuizz(quizz),Quizz.class));
    }
   
    public Mono<ServerResponse> deleteQuizz(ServerRequest request) {
    	String id = request.pathVariable("id");        

		return quizzService.getQuizzById(id)
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED)))
			.flatMap(quizz -> {
				return quizzService.deleteQuizzById(id).then(Mono.just(quizz));
			})
			.flatMap(deletedQuizz -> HandlerUtils.noContent);
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
			.flatMap(deletedQuizz -> HandlerUtils.noContent);
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
		    			.switchIfEmpty(HandlerUtils.serverError);
	    	});
    } 	
}
