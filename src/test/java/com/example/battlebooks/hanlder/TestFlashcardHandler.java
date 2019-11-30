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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.example.battlebooks.handler.HandlerUtils;
import com.example.battlebooks.model.Flashcard;
import com.example.battlebooks.model.QuestionCategory;
import com.example.battlebooks.model.QuestionType;
import com.example.battlebooks.repository.FlashcardRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
public class TestFlashcardHandler {
	
    final Logger logger = LogManager.getLogger(TestBookHandler.class.getSimpleName());

	@Autowired
    WebTestClient webTestClient;
	
    @Autowired
    FlashcardRepository cardRepo;
    
	
	List<Flashcard> cardList = Arrays.asList(
		new Flashcard("0001", QuestionType.IN_WHICH_BOOK, null,
				"In which book, a character name Fletcher is chased from his village for a crime he did not commit?",
				"The Novice by Taran Matharu", "The Novice"),
		new Flashcard("0002", QuestionType.CONTENT, QuestionCategory.CHARACTER,
				"In Frogkisser!, what is the question Anya was asked the most frequently, later in her life?",
				"How is it possible to have two stepparents and no actual parents?", 
				"Frogkisser!"),
		new Flashcard("0003", QuestionType.IN_WHICH_BOOK, null,
				"In which book, a character named Morven is turned to a Frog after being kissed?",
				"Frogkisser!",
				"Frogkisser!"),
		new Flashcard("0004", QuestionType.CONTENT, QuestionCategory.CHARACTER,
				"In The Only Road, how old is Jaime when he makes his journey from Guatemala to the United States?",
				"Twelve", 
				"The Only Road"),
		new Flashcard("0005", QuestionType.CONTENT, QuestionCategory.DATE,
				"In Port Chicago 50, when did a massive explosion rock the segregated Navy base at Port Chicago, California, killing more than 300 sailors?",
				"July 17, 1944", 
				"Port Chicago 50"),		
		new Flashcard("0006", QuestionType.AUTHOR, null,
				"Who is the author of the book The Only Road",
				"Alexandra Diaz", 
				"The Only Road"),
		new Flashcard("0007", QuestionType.CONTENT, QuestionCategory.OBJECT,
				"In Frogkisser!, What color kirtle is Anya wearing on her coranation day",
				"purple", 
				"Frogkisser!")
	);
				
	@AfterAll 
    public void cleanup() {
		cardRepo.deleteAll();
    }
    
    @BeforeAll
    public void setupTest() {
    	cardRepo.deleteAll()
            .thenMany(Flux.fromIterable(cardList))
            .flatMap(cardRepo::save)
            .doOnNext((card -> {
                logger.info("[setupTest] Saved card: "+ card);
            }))
            .blockLast();  // blocking ensures setup completes before we start any unit tests
    }
    
    private boolean equalStrings(String s1, String s2) {
    	return s1.equalsIgnoreCase(s2);
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByType() {
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_TYPE, QuestionType.AUTHOR)
        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(1);
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByMultipleTypes() {
    	
    	String types = QuestionType.AUTHOR + "," + QuestionType.CONTENT;
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_TYPE, types)
        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(5);
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByMultipleCategoriesInTypeContent() {
    	
    	String types = QuestionType.CONTENT;
    	String categories = QuestionCategory.OBJECT+","+QuestionCategory.DATE;
    	
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_TYPE, types)
        		.queryParam(Flashcard.KEY_CATEGORY, categories)

        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(2);
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByCategoryAndType() {
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_TYPE, QuestionType.CONTENT)
        		.queryParam(Flashcard.KEY_CATEGORY, QuestionCategory.CHARACTER)
        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(2);
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByBookTitle() {
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_BOOK, "Frogkisser!")
        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(3);
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByBookTitleAndCategory() {
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_BOOK, "Frogkisser!")
        		.queryParam(Flashcard.KEY_CATEGORY, QuestionCategory.CHARACTER)
        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(1)
        .consumeWith(response -> {
            List<Flashcard> cards = response.getResponseBody();
            cards.forEach(card -> 
            	assertTrue((equalStrings(card.getCategory(), QuestionCategory.CHARACTER) && equalStrings(card.getBookTitle(),"Frogkisser!"))));
        });
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByBookTitleAndMultipleCategories() {

        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_BOOK, "Frogkisser!")
        		.queryParam(Flashcard.KEY_CATEGORY, QuestionCategory.OBJECT+","+QuestionCategory.CHARACTER)
        		.build())
        .exchange()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .consumeWith( response -> {
            List<Flashcard> cards = response.getResponseBody();
            cards.forEach(card -> 
            	assertTrue(
            		(equalStrings(card.getCategory(), QuestionCategory.OBJECT) || equalStrings(card.getCategory(), QuestionCategory.CHARACTER)) &&
            		(equalStrings(card.getBookTitle(),"Frogkisser!")))
            	);	
        });  
    }
    
    @Test
    public void testGetByQuery_where_query_is_ByInvalidCategoryAndType() {
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_TYPE, QuestionType.IN_WHICH_BOOK)
        		.queryParam(Flashcard.KEY_CATEGORY, QuestionCategory.CHARACTER)
        		.build())
        .exchange()
        .expectStatus().is4xxClientError();
    }
    
    @Test
    public void testGetByQuery_where_query_is_ReturningEmptyResults() {
        webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_TYPE, QuestionType.CONTENT)
        		.queryParam(Flashcard.KEY_CATEGORY, QuestionCategory.EVENT)
        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(0);
    }
    
    @Test
    public void testGetByQuery_withAllQueryParams() {
    	webTestClient.get()
        .uri(uriBuilder -> uriBuilder
        		.path(HandlerUtils.API_CARDS)
        		.queryParam(Flashcard.KEY_TYPE, QuestionType.CONTENT)
        		.queryParam(Flashcard.KEY_CATEGORY, QuestionCategory.DATE)
        		.queryParam(Flashcard.KEY_BOOK, "Port Chicago 50")
        		.build())
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(Flashcard.class)
        .hasSize(1);
    }
    
    @Test
    public void testUpdateFlashCard() {
    	Flashcard card = new Flashcard("0007", QuestionType.CONTENT, QuestionCategory.OBJECT,
				"In Frogkisser!, What color kirtle is Anya wearing on her coranation day",
				"red", 
				"Frogkisser!");
    	
    	Flashcard updated = webTestClient.put()
                .uri(HandlerUtils.API_CARDS.concat("/{id}"), card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(card), Flashcard.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Flashcard.class)
                .returnResult()
                .getResponseBody();    
         logger.info("testUpdateBook: updated book {}",updated.toString());   
       	 assertTrue(equalStrings(card.getCategory(), updated.getCategory()) && equalStrings(card.getAnswer(), updated.getAnswer()));      
    }
    
    @Test
    public void testUpdateFlashCard_when_cardIsNotFound() {
    	Flashcard card = new Flashcard("9999", QuestionType.CONTENT, QuestionCategory.OBJECT,
				"In Frogkisser!, What color kirtle is Anya wearing on her coranation day",
				"red", 
				"Frogkisser!");
    	
    	webTestClient.put()
                .uri(HandlerUtils.API_CARDS.concat("/{id}"), card.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(card), Flashcard.class)
                .exchange()
                .expectStatus().is4xxClientError();    
    }
    
    @Test
    public void testCreateBook() {
    	 Flashcard card = new Flashcard()
 	    		.setId("0008")
 	    		.setCategory(QuestionCategory.PLACE)
 	    		.setType(QuestionType.CONTENT)
 	    		.setQuestion("In Port Chicago 50, in which state is the Navy base located?")
 	    		.setAnswer("California")
 	    		.setBookTitle("Port Chicago 50");
 	    		
    	 
    	 Flashcard created = webTestClient.post()
            .uri(HandlerUtils.API_CARDS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(card), Flashcard.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Flashcard.class)
            .returnResult()
            .getResponseBody();
    	 
    	 assertTrue(created.getId().equals(card.getId()) && created.getAnswer().equals(card.getAnswer()));      
    }
    
    @Test
    public void testCreateBook_where_bookTitle_is_invalid() {
    	 Flashcard card = new Flashcard()
 	    		.setId("0008")
 	    		.setCategory(QuestionCategory.PLACE)
 	    		.setType(QuestionType.CONTENT)
 	    		.setQuestion("In Port Chicago 50, in which state is the Navy base located?")
 	    		.setAnswer("California")
 	    		.setBookTitle("This book does not exist");
 	    		
    	 
    	 webTestClient.post()
            .uri(HandlerUtils.API_CARDS)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(card), Flashcard.class)
            .exchange()
            .expectStatus().isBadRequest();    
    }
    
}
