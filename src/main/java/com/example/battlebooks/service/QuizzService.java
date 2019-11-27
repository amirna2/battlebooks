package com.example.battlebooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.battlebooks.model.Quizz;
import com.example.battlebooks.repository.QuizzRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class QuizzService {

	@Autowired QuizzRepository quizzRepo;
	@Autowired BookService bookService;
	@Autowired FlashcardService cardService;
	
	public QuizzService() {}
	
	public Flux<Quizz> getAllQuizzes() {
		return quizzRepo.findAll();
	}

	public Mono<Quizz> getQuizzById(String id) {
		return quizzRepo.findById(id);
	}

	public Mono<Void> deleteQuizzById(String id) {
		return quizzRepo.deleteById(id);
	}

	public Mono<Quizz> saveQuizz(Quizz quizz) {
		
		//TODO: Actually populate the Quizz flashcards list based in the book Ids, before saving it in the database
		return quizzRepo.save(quizz);
	} 
}
