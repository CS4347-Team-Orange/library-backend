package edu.utdallas.cs4347.library.domain;

public class BookAuthor {

    private String bookId;

    private String authorId;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "BookAuthor{" +
                "bookId='" + bookId + '\'' +
                ", authorId='" + authorId + '\'' +
                '}';
    }

}
