package com.example.battlebooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.battlebooks.model.Book;
import com.example.battlebooks.repository.BookRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepo;
	
	public BookService() {
				
	}

	public Flux<Book> getAllBooks() {
		return bookRepo.findAll();
	}
	
	public Mono<Book> getBookById(String id) {
		return bookRepo.findById(id);
	}
	
	public Mono<Void> deleteBookById(String id) {
		return bookRepo.deleteById(id);
	}
	
	public Mono<Book> saveBook(Book book) {
		return bookRepo.save(book);
	}
	
	public Mono<Boolean> bookExists(String id) {
		return bookRepo.existsById(id);
	}
}
