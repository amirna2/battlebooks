package com.example.battlebooks.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="flashcards")
public class Flashcard {

	@Id private String id;
	
	public static final String KEY_TYPE = "type";
	@Indexed private String type;
	
	public static final String KEY_CATEGORY = "category";
	@Indexed private String category;
	
	private String question;
	private String answer;
	
	public static final String KEY_BOOK = "bookTitle";
	@TextIndexed private String bookTitle;
	
	public Flashcard() {}
	
	public Flashcard(String id, String type, String category, String question, String answer, String bookTitle) {
		this.id = id;
		this.type = type;
		this.category = category;
		this.question = question;
		this.answer = answer;
		this.bookTitle = bookTitle;
	}

	@Override
	public String toString() {
		return String.format("Flashcard [id=%s, type=%s, category=%s, question=%s, answer=%s, bookTitle=%s]", id, type,
				category, question, answer, bookTitle);
	}

	public String getId() {
		return id;
	}

	public Flashcard setId(String id) {
		this.id = id;
	    return this;
	}

	public String getType() {
		return type;
	}

	public Flashcard setType(String type) {
		this.type = type;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public Flashcard setCategory(String category) {
		this.category = category;
		return this;
	}

	public String getQuestion() {
		return question;
	}

	public Flashcard setQuestion(String question) {
		this.question = question;
		return this;
	}

	public String getAnswer() {
		return answer;
	}

	public Flashcard setAnswer(String answer) {
		this.answer = answer;
		return this;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public Flashcard setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
		return this;
	}
	
}
