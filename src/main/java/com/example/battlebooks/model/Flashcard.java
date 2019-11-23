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
	
	public Flashcard(String id, String type, String category, String question, String answer, String bookTitle) {
		this.id = id;
		this.type = type;
		this.category = category;
		this.question = question;
		this.answer = answer;
		this.bookTitle = bookTitle;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
	
	@Override
	public String toString() {
		return "Flashcard [id=" + id + ", type=" + type + ", category=" + category + ", question=" + question
				+ ", answer=" + answer + "]";
	}
	
}
