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

import com.example.battlebooks.model.Flashcard;
import com.example.battlebooks.model.QuestionCategory;
import com.example.battlebooks.model.QuestionType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
public class TestFlashcardRepository {

	@Autowired FlashcardRepository cardRepo;
	
	@AfterAll 
    public void testCleanup() {
		cardRepo.deleteAll();
    }
    
    @BeforeAll
    public void testSetup() {
    	List<Flashcard> cardList = Arrays.asList(new Flashcard("0001", QuestionType.IN_WHICH_BOOK, null,
    			"In which book, a character name Fletcher is chased from his village for a crime he did not commit?",
    			"The Novice by Taran Matharu", "The Novice"),
    			new Flashcard("0002", QuestionType.CONTENT, QuestionCategory.CHARACTER,
    					"In Frogkisser!, what is the question Anya was asked the most frequently, later in her life?",
    					"How is it possible to have two stepparents and no actual parents?", "Frogkisser!"),
    			new Flashcard("0003", QuestionType.IN_WHICH_BOOK, null,
    					"In which book, a character named Morven is turned to a Frog after being kissed?",
    					"Frogkisser!", "Frogkisser!"),
    			new Flashcard("0004", QuestionType.CONTENT, QuestionCategory.CHARACTER,
    					"In The Only Road, how old is Jaime when he makes his journey from Guatemala to the United States?",
    					"Twelve", "The Only Road"),
    			new Flashcard("0005", QuestionType.CONTENT, QuestionCategory.DATE,
    					"In Port Chicago 50, when did a massive explosion rock tthe segregated Navy base at Port Chicago, California, killing more than 300 sailors?",
    					"July 17, 1944", "Port Chicago 50"),
    			new Flashcard("0006", QuestionType.AUTHOR, null, "Who is the author of the book The Only Road",
    					"Alexandra Diaz", "The Only Road"));
    	
    	cardRepo.deleteAll().thenMany(Flux.fromIterable(cardList)).flatMap(cardRepo::save).doOnNext((card -> {
			System.out.println("[setupTest] Saved card: " + card);
		})).blockLast(); // blocking only for testing purposes
    }
    
    @Test
    public void findAll() {
        Flux<Flashcard> cardFlux = cardRepo.findAll();
        
        StepVerifier.create(cardFlux)
        .expectSubscription()
        .expectNextCount(16)
        .expectComplete();
    }
    
    @Test
    public void findCardById() {
        Mono<Flashcard> cardFlux = cardRepo.findById("001");
        
        StepVerifier.create(cardFlux)
        .expectSubscription()
        .expectNextMatches((qna) -> qna.getQuestion().startsWith("In which book, a CHARACTER name Fletcher"))
        .expectComplete();
    }
    
    @Test
    public void findCardByBookTitle() {
        Flux<Flashcard> cardFlux = cardRepo.findByBookTitle("Frogkisser!");
        StepVerifier.create(cardFlux)
                .expectSubscription()
                .expectNextCount(2)
                .verifyComplete();
    }
    
    @Test
    public void findCardByCategory() {
        Flux<Flashcard> cardFlux = cardRepo.findByCategory(QuestionCategory.DATE);
        StepVerifier.create(cardFlux)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }
    
    @Test
    public void findCardByType() {
        Flux<Flashcard> cardFlux = cardRepo.findByType(QuestionType.CONTENT);
        StepVerifier.create(cardFlux)
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
    
	@Test
    public void addNewCard() {
		Flashcard card = new Flashcard("9999", QuestionType.AUTHOR, null,
				"Who is the author of book The Novice?",
				"Taran Matharu", 
				"The Novice");
		Mono<Flashcard> saved = cardRepo.save(card);
		System.out.println(saved.toString());
	    
	    StepVerifier.create(saved)
	    .expectSubscription()
	    .expectNextMatches( (savedCard) -> 
	        savedCard.getId().equals(card.getId()) && savedCard.getAnswer().equals(savedCard.getAnswer()) )
	    .verifyComplete();
	    
    }
	
	@Test
	public void deleteCardById() {
		Mono<Void> deleted = cardRepo.findById("0001")
				// map transforms the Flux book into a book OBJECT which can be manipulated
				.map(Flashcard::getId).flatMap(id -> {
					return cardRepo.deleteById(id);
				});

		StepVerifier.create(deleted.log("[deleteCardById] ")).expectSubscription().verifyComplete();

		Mono<Flashcard> card = cardRepo.findById("011").log("[deleteCardById] verify: ");
		StepVerifier.create(card)
			.expectSubscription()
			.expectNextCount(0)
			.verifyComplete();
	}
}
