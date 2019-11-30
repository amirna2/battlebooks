package com.example.battlebooks.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.battlebooks.model.Flashcard;

import reactor.core.publisher.Flux;

public interface FlashcardRepository extends ReactiveMongoRepository<Flashcard, String> {

	Flux<Flashcard> findByType(String type);
	Flux<Flashcard> findByCategory(String category);
	Flux<Flashcard> findByBookTitle(String string);
}