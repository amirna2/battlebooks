package com.example.battlebooks.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.battlebooks.model.Book;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
public class TestBookRepository {

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
                System.out.println("[setupTest] Saved Book: "+ book);
            }))
            .blockLast();  // blocking only for testing purposes
    }
    
    @Test
    public void findAll() {
        Flux<Book> bookFlux = bookRepo.findAll();
        
        StepVerifier.create(bookFlux)
        .expectSubscription()
        .expectNextCount(16)
        .expectComplete();
    }
    
    @Test
    public void findById() {
        Mono<Book> bookFlux = bookRepo.findById("001");
        
        StepVerifier.create(bookFlux)
        .expectSubscription()
        .expectNextMatches((book) -> book.getTitle().equals("The Blackthorn Key"))
        .expectComplete();
    }
    
    @Test
    public void findByAuthor() {
        Mono<Book> bookMono = bookRepo.findByAuthor("Patricia Forde").log("[findByAuthor] ");
        StepVerifier.create(bookMono)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }   
    
    @Test
    public void findByTitle() {
        Mono<Book> bookMono = bookRepo.findByTitle("The Blackthorn Key").log("[findByTitle] ");
        StepVerifier.create(bookMono)
                .expectSubscription()
                .expectNextMatches(book -> book.getAuthor().equals("Kevin Sands"))
                .verifyComplete();
    }
    
    @Test
    public void updateBook() {
        String description = "Fletcher is working as a blacksmith’s apprentice when he discovers he has the rare ability to summon demons from another world.";
        Mono<Book> updated = bookRepo.findByTitle("The Novice")
        // map transforms the Flux Book into a Book OBJECT which can be manipulated
        .map(book -> {
            book.setDescription(description); // update the item with the new price and return it
            return book;
        })
        .flatMap( book -> {
            return bookRepo.save(book); // saves the book back into the database
        });
        
        StepVerifier.create(updated)
        .expectSubscription()
        .expectNextMatches( (book) -> book.getDescription().startsWith("Fletcher"))
        .verifyComplete();
    }
    
	@Test
	public void deleteBookById() {
		Mono<Void> deleted = bookRepo.findById("011")
				// map transforms the Flux book into a book OBJECT which can be manipulated
				.map(Book::getId).flatMap(id -> {
					return bookRepo.deleteById(id);
				});

		StepVerifier.create(deleted.log("[deleteBookById] ")).expectSubscription().verifyComplete();

		Mono<Book> book = bookRepo.findById("011").log("[deleteBookById] verify: ");
		StepVerifier.create(book)
			.expectSubscription()
			.expectNextCount(0)
			.verifyComplete();
	}
	
	@Test
    public void addNewBook() {
	    Book book = new Book()
	    		.setId("999")
	    		.setTitle("A new book title")
	    		.setAuthor("Anonymous")
	    		.setScore(0.0f)
	    		.setCover(null)
	    		.setDescription("Blah...blah...");
	    
	    Mono<Book> saved = bookRepo.save(book);
	    
	    StepVerifier.create(saved)
	    .expectSubscription()
	    .expectNextMatches( (savedBook) -> 
	        savedBook.getId().equals(book.getId()) && savedBook.getTitle().equals(savedBook.getTitle()) )
	    .verifyComplete();
    }
}
