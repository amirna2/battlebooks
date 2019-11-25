package com.example.battlebooks.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.example.battlebooks.model.Flashcard;
import com.example.battlebooks.model.QuestionType;
import com.example.battlebooks.repository.FlashcardRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlashcardService {

	@Autowired
	FlashcardRepository cardRepo;
	@Autowired
	ReactiveMongoTemplate template;

	public FlashcardService() {

	}

	public Flux<Flashcard> getAllCards() {
		return cardRepo.findAll();
	}

	public Mono<Flashcard> getCardById(String id) {
		return cardRepo.findById(id);
	}

	public Mono<Void> deleteCardById(String id) {
		return cardRepo.deleteById(id);
	}

	public Mono<Flashcard> saveCard(Flashcard card) {
		return cardRepo.save(card);
	}

	public Flux<Flashcard> getCardsByQuery(MultiValueMap<String, String> queryParams) {

		if (queryParams.isEmpty()) {
			return getAllCards();
		}

		String type = queryParams.getFirst(Flashcard.KEY_TYPE);
		String category = queryParams.getFirst(Flashcard.KEY_CATEGORY);
		String book = queryParams.getFirst(Flashcard.KEY_BOOK);

		// There is no category when the question is "In which book...."
		// This should be checked from the front end, but we'll return a 400 status in
		// any case.
		if (type != null && type.equals(QuestionType.IN_WHICH_BOOK) && category != null) {
			return null;
		}

		// We have some query parameters, let's build query and find some results
		Query query = new Query();

		// we can have comma separated strings for each query param. e.g
		// type="content,author"
		if (type != null) {
			List<String> paramList = Arrays.asList(type.split("\\s*,\\s*"));
			query.addCriteria(Criteria.where(Flashcard.KEY_TYPE).in(paramList));
		}

		if (category != null) {
			List<String> paramList = Arrays.asList(category.split("\\s*,\\s*"));
			query.addCriteria(Criteria.where(Flashcard.KEY_CATEGORY).in(paramList));
		}
		if (book != null) {
			List<String> paramList = Arrays.asList(book.split("\\s*,\\s*"));
			query.addCriteria(Criteria.where(Flashcard.KEY_BOOK).in(paramList));
		}

		// example query:
		// Query: { "type" : { "$in" : ["content"]}, "category" : { "$in" : ["object",
		// "date"]}}, Fields: {}, Sort: {}
		Flux<Flashcard> cards = template.find(query, Flashcard.class);

		return cards;
	}
}
