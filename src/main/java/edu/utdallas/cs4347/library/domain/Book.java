package edu.utdallas.cs4347.library.domain;

import java.util.*;
import javax.persistence.*;

@Entity
public class Book {

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getIsbn10() {
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setCheckedOut(boolean b) { 
        this.checkedOut = b;
    }

    public boolean getCheckedOut() { 
        return this.checkedOut;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
        this.authorString = getAuthorString();
    }

    public List<Author> getAuthors() {
        return this.authors;
    }

    public String getAuthorString() {
        List<String> authorNames = new ArrayList<String>();
        for (Author a : getAuthors()) {
            authorNames.add(a.getName());
        }
        String s = String.join(", ", authorNames);
        return s;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", isbn10='" + isbn10 + '\'' +
                ", isbn13='" + isbn13 + '\'' +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pages=" + pages +
                ", checkedOut=" + checkedOut +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String bookId;

    private String isbn10;
    private String isbn13;
    private String title;
    private String cover;
    private String publisher;
    private int pages;
    private boolean checkedOut;


    @Transient
    private List<Author> authors;

    @Transient
    private String authorString;

}
