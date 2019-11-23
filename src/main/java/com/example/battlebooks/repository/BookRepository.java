package com.example.battlebooks.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.battlebooks.model.Book;

import reactor.core.publisher.Mono;

public interface BookRepository extends ReactiveMongoRepository<Book, String> {

	Mono<Book> findByAuthor(String string);
	Mono<Book> findByTitle(String string);
}