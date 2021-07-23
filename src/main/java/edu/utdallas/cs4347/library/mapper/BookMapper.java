package edu.utdallas.cs4347.library.mapper;
import java.util.*;
import edu.utdallas.cs4347.library.domain.Book;

public interface BookMapper {
    List<Book> getAll();

    Book getOneById(String bookId);

    void insert(Book book);

    void update(Book book);

    void delete(String bookId);
}