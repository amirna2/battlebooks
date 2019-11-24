package com.example.battlebooks.model;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="books")
public class Book {
	
	public static final String KEY_BOOK_ID = "id";
	@Id private String id;
	
	public static final String KEY_BOOK_TITLE = "title";
	@TextIndexed private String title;
	
	@Indexed private Float  score;
	
	private String author;
	private String description;
	private Binary cover;
	    
	public Book() {}
	
    public Book(String title, String author, String description, Binary cover) {
		this.title = title;
		this.author = author;
		this.score = 0f;
		this.description = description;
		this.cover = cover;
	}

    public Book(String id, String title, String author, String description, Binary cover) {
    	this.id = id;
		this.title = title;
		this.author = author;
		this.score = 0f;
		this.description = description;
		this.cover = cover;
	}
    
	public String getId() {
		return id;
	}

	public Book setId(String id) {
		this.id = id;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Book setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getAuthor() {
		return author;
	}

	public Book setAuthor(String author) {
		this.author = author;
		return this;
	}

	public Float getScore() {
		return score;
	}

	public Book setScore(Float score) {
		this.score = score;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Book setDescription(String description) {
		this.description = description;
		return this;
	}

	public Binary getCover() {
		return cover;
	}

	public Book setCover(Binary cover) {
		this.cover = cover;
		return this;
	}


	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", AUTHOR=" + author + ", score=" + score + ", description="
				+ description + ", cover=" + cover+"]";
	}
}
