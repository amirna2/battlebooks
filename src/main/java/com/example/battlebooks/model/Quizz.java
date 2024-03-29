package com.example.battlebooks.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="quizzes")
public class Quizz {

	public static final String KEY_QUIZZ_ID = "id";
	@Id private String id;
	
	private String name;  // human readable name
	private String description; // a brief description of the quizz
	private List<String> cardIds; // generated list of Flashcard IDs
	@Transient
	private Map<String, Integer> scoreSheet; // keeps track of answers per card ID. 0 -> wrong answer, 1 -> correct answer
	@Transient
	private int totalScore; // total score on that quizz

	// These 2 properties are inputs to the Quizz builder
	private int cardTimeout; // amount of time allowed to answer the question on the card. 0 means the card is not timed
	private List<String> bookIds;

	public Quizz(String id, List<String> bookIds, int cardTimeout, String name, String description, List<String> cardIds) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.cardIds = cardIds;
		this.cardTimeout = cardTimeout;
		this.bookIds = bookIds;
	}
	
	public Quizz() {}
	
	
	public String getId() {
		return id;
	}
	public Quizz setId(String id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public Quizz setName(String name) {
		this.name = name;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Quizz setDescription(String description) {
		this.description = description;
		return this;
	}
	public List<String> getCardIds() {
		return cardIds;
	}
	public Quizz setCardIds(List<String> cardIds) {
		this.cardIds = cardIds;
		return this;
	}
	public int getCardTimeout() {
		return cardTimeout;
	}
	public Quizz setCardTimeout(int cardTimeout) {
		this.cardTimeout = cardTimeout;
		return this;
	}
	public Map<String, Integer> getScoreSheet() {
		return scoreSheet;
	}
	public Quizz setScoreSheet(Map<String, Integer> scoreSheet) {
		this.scoreSheet = scoreSheet;
		return this;
	}
	public int getTotalScore() {
		return totalScore;
	}
	public Quizz setTotalScore(int totalScore) {
		this.totalScore = totalScore;
		return this;
	}
	
	public Quizz setBookIds(List<String> bookIds) {
		this.bookIds = bookIds;
		return this;
	}
	public List<String> getBookIds() {
		return bookIds;
	}
	
	@Override
	public String toString() {
		return String.format(
				"Quizz [id=%s, name=%s, description=%s, bookIds=%s cardIds=%s, cardTimeout=%s, scoreSheet=%s, totalScore=%s]", id,
				name, description, bookIds, cardIds, cardTimeout, scoreSheet, totalScore);
	}
}
