package com.example.battlebooks.hanlder;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.battlebooks.handler.BookHandler;
import com.example.battlebooks.model.Book;
import com.example.battlebooks.repository.BookRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
public class BookHandlerTest {
	
    final Logger logger = LogManager.getLogger(BookHandlerTest.class.getSimpleName());

	@Autowired
    WebTestClient webTestClient;
	
    @Autowired
    BookRepository bookRepo;
	
    List<Book> books = Arrays.asList(
			new Book("001","The Blackthorn Key" ,"Kevin Sands", "Following a series of murders, an apothecary’s apprentice must solve puzzles and decipher codes in pursuit of a secret that could destroy the world in this “spectacular debut“", null),
			new Book("002","Falling Over Sideways" ,"Jordan Sonnenblick", "A new hilarious, honest, and hopeful novel from the AUTHOR of Drums, Girls, and Dangerous Pie. It's not easy being Claire...Really.", null),
			new Book("003","The First Rule of Punk" ,"Celia C. Pérez", "", null),
			new Book("004","Frogkisser!" ,"Garth Nix", "", null),
			new Book("005","The Girl Who Drank the Moon" ,"Kelly Barnhill", "", null),
			new Book("006","House Arrest" ,"K.A. Holt", "Touching, humorous, and always original, House Arrest is a funny book for teens in verse about a good boy's hard won path to redemption.", null),
			new Book("007","Insignificant Events in the Life of a Cactus" ,"Dusti Bowling", "", null),
			new Book("008","It Ain’t So Awful, Falafel" ,"Firoozeh Dumas", "", null),
			new Book("009","The List" ,"Patricia Forde", "", null),
			new Book("010","Ms. Bixby’s Last Day" ,"David Anderson", "", null),
			new Book("011","The Novice" ,"Taran Matharu", "", null),
			new Book("012","The Only Road" ,"Alexandra Diaz", "", null),
			new Book("013","The Port Chicago 50" ,"Steve Sheinkin", "", null),
			new Book("014","The School for Good and Evil" ,"Soman Chainani", "", null),
			new Book("015","The Shadow Cipher","Laura Ruby", "", null),
			new Book("016","The Teacher’s Funeral" ,"Richard Peck", "", null)			
	);
				
	@AfterAll
    public void cleanup() {
		bookRepo.deleteAll();
    }
    
    @BeforeAll
    public void setupTest() {
    	bookRepo.deleteAll()
            .thenMany(Flux.fromIterable(books))
            .flatMap(bookRepo::save)
            .doOnNext((book -> {
                logger.info("[setupTest] Saved Book: {} ",book);
            }))
            .blockLast();  // blocking only for testing purposes
    }
    
    @Test
    public void testGetAllBooks() {
        webTestClient.get()
            .uri(BookHandler.API_BOOKS)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Book.class)
            .hasSize(books.size());
    }
    
    @Test
    public void testGetAllBooks_withVerifier() {
        Flux<Book> bookFlux = webTestClient.get()
            .uri(BookHandler.API_BOOKS)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(Book.class)
            .getResponseBody();

        StepVerifier.create(bookFlux.log())
        .expectSubscription()
        .expectNextCount(books.size())
        .verifyComplete();
    }
    
    @Test
    public void test_GetBookById_when_IdExists() {
       Book book = webTestClient.get()
            .uri(BookHandler.API_BOOKS.concat("/{id}"), "004")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Book.class)
            .returnResult()
            .getResponseBody();
       
       assertTrue(book.getTitle().equals("Frogkisser!") && book.getId().equals("004"));   
    }
    
    @Test
    public void test_GetBookById_when_IdDoesNotExist() {
       Book book = webTestClient.get()
            .uri(BookHandler.API_BOOKS.concat("/{id}"), "100")
            .exchange()
            .expectStatus().is4xxClientError()
            .expectBody(Book.class)
            .returnResult()
            .getResponseBody();
       
       assertTrue(book == null);   
    }
    
    @Test
    public void testCreateBook() {
    	 Book book = new Book()
 	    		.setId("999")
 	    		.setTitle("A new book title")
 	    		.setAuthor("Anonymous")
 	    		.setScore(0.0f)
 	    		.setCover(null)
 	    		.setDescription("Blah...blah...");
    	 
    	 Book created = webTestClient.post()
            .uri(BookHandler.API_BOOKS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(book), Book.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class)
            .returnResult()
            .getResponseBody();
    	 
    	 assertTrue(created.getId().equals(book.getId()) && created.getAuthor().equals(book.getAuthor()));      
    }
    
    @Test
    public void testDeleteBook() {
        webTestClient.delete()
        .uri(BookHandler.API_BOOKS.concat("/{id}"), "004")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNoContent();
    }
    
    @Test
    public void deleteItem_NotAllowed() {
        webTestClient.delete()
        .uri(BookHandler.API_BOOKS.concat("/{id}"), "300")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    @Test
    public void testUpdateBook() {
      
    	Book book = new Book("016","The Teacher’s Funeral" ,"Richard Peck", "some description...", null);
        Book updated = webTestClient.put()
            .uri(BookHandler.API_BOOKS.concat("/{id}"), book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(book), Book.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Book.class)
            .returnResult()
            .getResponseBody();    
        
   	 assertTrue(updated.getAuthor().equals(book.getAuthor()) && updated.getDescription().equals(book.getDescription()));      

    }
    
    @Test
    public void testUpdateBook_NotFound() {
      
    	Book book = new Book("100","The Teacher’s Funeral" ,"Richard Peck", "some description...", null);
        webTestClient.put()
            .uri(BookHandler.API_BOOKS.concat("/{id}"), book.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(book), Book.class)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
        	
    }
}
