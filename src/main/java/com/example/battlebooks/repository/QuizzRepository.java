package com.example.battlebooks.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.battlebooks.model.Quizz;

public interface QuizzRepository extends ReactiveMongoRepository<Quizz, String> {
	

}
