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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.battlebooks.model.Quizz;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
public class TestQuizzRepository {

	@Autowired
	QuizzRepository quizzRepo;
	
	@Autowired
	MongoTemplate template;
	
	List<Quizz> quizzes = Arrays.asList(
			new Quizz("001", "test quizz 1", "A test quizz", Arrays.asList("002","004","005"),0),
			new Quizz("002", "test quizz 2", "A test quizz with only in which book questions", Arrays.asList("001","003"),30));
	
				
	@AfterAll 
    public void cleanup() {
		quizzRepo.deleteAll();
    }
    
    @BeforeAll
    public void setupTest() {
    	quizzRepo.deleteAll()
            .thenMany(Flux.fromIterable(quizzes))
            .flatMap(quizzRepo::save)
            .doOnNext((quizz -> {
                System.out.println("[setupTest] Saved Book: "+ quizz);
            }))
            .blockLast();  // blocking only for testing purposes
    }
    
    @Test
    public void findAll() {
        Flux<Quizz> quizzFlux = quizzRepo.findAll();
        
        StepVerifier.create(quizzFlux)
        .expectSubscription()
        .expectNextCount(2)
        .expectComplete();
    }
    
    @Test
    public void findById() {
        Mono<Quizz> quizzFlux = quizzRepo.findById("001");
        
        StepVerifier.create(quizzFlux)
        .expectSubscription()
        .expectNextMatches((quizz) -> quizz.getName().equals("test quizz 1"))
        .expectComplete();
    }
    
    @Test
    public void updateQuizz() {
        Integer timeout = 60;
        Mono<Quizz> updated = quizzRepo.findById("001")
        .map(quizz -> {
            quizz.setCardTimeout(timeout); 
            return quizz;
        })
        .flatMap( quizz -> {
            return quizzRepo.save(quizz);
        });
        
        StepVerifier.create(updated)
        .expectSubscription()
        .expectNextMatches( quizz -> quizz.getCardTimeout() == timeout)
        .verifyComplete();
    }
    
	@Test
	public void deleteQuizzById() {
		Mono<Void> deleted = quizzRepo.findById("002")
				// map transforms the Flux book into a book OBJECT which can be manipulated
				.map(Quizz::getId).flatMap(id -> {
					return quizzRepo.deleteById(id);
				});

		StepVerifier.create(deleted.log("[deleteQuizzById] ")).expectSubscription().verifyComplete();

		Mono<Quizz> book = quizzRepo.findById("002").log("[deleteQuizzById] verify: ");
		StepVerifier.create(book)
			.expectSubscription()
			.expectNextCount(0)
			.verifyComplete();
	}
	
	@Test
    public void addNewQuizz() {
	    Quizz quizz = new Quizz("999", "test quizz 3", "A test quizz", Arrays.asList("002","004","005","007"),60);

	    Mono<Quizz> saved = quizzRepo.save(quizz);
	    
	    StepVerifier.create(saved)
	    .expectSubscription()
	    .expectNextMatches( savedQuizz -> 
	        savedQuizz.getId().equals(quizz.getId()) )
	    .verifyComplete();
    }
}
