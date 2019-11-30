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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.battlebooks.handler.HandlerUtils;
import com.example.battlebooks.model.Quizz;
import com.example.battlebooks.repository.QuizzRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("dev")
@TestInstance(Lifecycle.PER_CLASS)
public class TestQuizzHandler {

	final Logger logger = LogManager.getLogger(TestQuizzHandler.class.getSimpleName());

	List<Quizz> quizzes = Arrays.asList(
			new Quizz("001", Arrays.asList("004", "012","014"), 0, "quizz 1", "A test quizz", Arrays.asList("0002","0004","0005")),
			new Quizz("002", Arrays.asList("004","011"), 30, "quizz 2", "in which book questions only", Arrays.asList("0001","0003"))
	);
	
	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
    QuizzRepository quizzRepo;
	
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
                logger.info("[setupTest] Saved Quizz: {} ",quizz);
            }))
            .blockLast();  // blocking only for testing purposes
    }
    
    
    @Test
    public void testGetAllQuizzes() {
        Flux<Quizz> quizzFlux = webTestClient.get()
            .uri(HandlerUtils.API_QUIZZES)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(Quizz.class)
            .getResponseBody().log();

        StepVerifier.create(quizzFlux.log())
        .expectSubscription()
        .expectNextCount(quizzes.size())
        .verifyComplete();
    }
    
    @Test
    public void testGetQuizzById_when_IdExists() {
       Quizz quizz = webTestClient.get()
            .uri(HandlerUtils.API_QUIZZES.concat("/{id}"), "001")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Quizz.class)
            .returnResult()
            .getResponseBody();
       
       assertTrue(quizz.getId().equals("001"));   
    }
	
    @Test
    public void testGetQuizzById_when_IdDoesNotExist() {
       Quizz quizz = webTestClient.get()
            .uri(HandlerUtils.API_QUIZZES.concat("/{id}"), "100")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(Quizz.class)
            .returnResult()
            .getResponseBody();
       
       assertTrue(quizz == null);   
    }
    
    @Test
    public void testCreateQuizz() {
    	Quizz quizz = new Quizz()
    			.setId("200")
    			.setName("A new quizz")
    			.setDescription("A random quizz for testing")
    			.setBookIds(Arrays.asList("004", "012","014"))
    			.setCardIds(Arrays.asList("0001","0003"))
    			.setCardTimeout(60)
    			.setScoreSheet(null)
    			.setTotalScore(0);
    	
    	Quizz created = webTestClient.post()
                .uri(HandlerUtils.API_QUIZZES)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(quizz), Quizz.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Quizz.class)
                .returnResult()
                .getResponseBody();
        	 
        assertTrue(created.getId().equals(quizz.getId()) && created.getName().equals(quizz.getName()));      
    }
    
    @Test
    public void testDeleteQuizz() {
        webTestClient.delete()
        .uri(HandlerUtils.API_QUIZZES.concat("/{id}"), "001")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isNoContent();
    }
    
    @Test
    public void deleteQuizz_NotAllowed() {
        webTestClient.delete()
        .uri(HandlerUtils.API_QUIZZES.concat("/{id}"), "300")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    @Test
    public void testUpdateQuizz() {
      
    	Quizz quizz = new Quizz("001", Arrays.asList("004", "012","014"), 0, "quizz 1 updated", "A test quizz", Arrays.asList("0002","0004","0005"));
    	    	
    	Quizz updated = webTestClient.put()
            .uri(HandlerUtils.API_QUIZZES.concat("/{id}"), quizz.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(quizz), Quizz.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Quizz.class)
            .returnResult()
            .getResponseBody();    
     logger.info("[testUpdateQuizz] updated quizz {}",updated.toString());   
   	 assertTrue(updated.getName().equals(quizz.getName()) && updated.getCardTimeout() == quizz.getCardTimeout());      

    }
    
    @Test
    public void testUpdateBook_NotFound() {
      
    	Quizz quizz = new Quizz("999", Arrays.asList("004", "012","014"), 0, "quizz 1 updated", "A test quizz", Arrays.asList("0002","0004","0005"));

    	
    	webTestClient.put()
            .uri(HandlerUtils.API_QUIZZES.concat("/{id}"), quizz.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(quizz), Quizz.class)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
        	
    }
}
