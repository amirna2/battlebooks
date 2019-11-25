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
import com.example.battlebooks.model.Book;
import com.example.battlebooks.repository.BookRepository;
import com.example.battlebooks.service.BookService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BookHandler {

	public static final String  API_BOOKS = "/api/books";
    final Logger logger = LogManager.getLogger(BookHandler.class.getSimpleName());
    
	static final Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	static final Mono<ServerResponse> badRequest = ServerResponse.badRequest().build();
	static final Mono<ServerResponse> notAllowed = ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	static final Mono<ServerResponse> noContent = ServerResponse.noContent().build();
	static final Mono<ServerResponse> serverError = ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	@Autowired
	BookService bookService;
	
    public Mono<ServerResponse> getAllBooks(ServerRequest request) {
    	
    	logger.info("getAllBooks: request {}",request.toString());
    	
    	Flux<Book> books = bookService.getAllBooks();
    	
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(books, Book.class);
    }

    public Mono<ServerResponse> getBookById(ServerRequest request) {
        String id = request.pathVariable("id");
        
        Mono<Book> foundBook = bookService.getBookById(id);
        
        return foundBook.flatMap(book -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(book)))
                    .switchIfEmpty(notFound);
    }
        
    public Mono<ServerResponse> createBook(ServerRequest request) {
        Mono<Book> bookToCreate = request.bodyToMono(Book.class);
        
        return bookToCreate.flatMap(book -> 
            ServerResponse.created(null)
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookService.saveBook(book),Book.class));
    }
   
    public Mono<ServerResponse> deleteBook(ServerRequest request) {
    	String id = request.pathVariable("id");        

		return bookService.getBookById(id)
			.switchIfEmpty(error(new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED)))
			.flatMap(book -> {
				return bookService.deleteBookById(id).then(Mono.just(book));
			})
			.flatMap(deletedBook -> noContent);
	}
    
    public Mono<ServerResponse> updateBook(ServerRequest request) {
	    String id = request.pathVariable("id");	    
	    final String notFoundReason = "Book ID "+id+" does not exist";
	    
	    Mono<Boolean> exists = bookService.bookExists(id);

	    /*	    
	    return bookService.getBookById(id)
	    		  .switchIfEmpty(error(new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundReason)))
	    		  .then(request.bodyToMono(Book.class))
	    		  .flatMap( update -> bookService.saveBook(update))
	    		  .flatMap(saved -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(saved)))
				  .switchIfEmpty(serverError);
	     */

	    return exists.
	    		flatMap(bookExists -> {
	    			if( !bookExists) {
	    				return error(new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundReason));
	    			}
		    		return request.bodyToMono(Book.class)
		    			.flatMap( update -> bookService.saveBook(update))
		    			.flatMap(saved -> ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(BodyInserters.fromValue(saved)))
		    			.switchIfEmpty(serverError);
		    	
	    	});
    } 	
}
