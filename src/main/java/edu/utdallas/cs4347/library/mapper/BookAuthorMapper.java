package edu.utdallas.cs4347.library.mapper;

import edu.utdallas.cs4347.library.domain.BookAuthor;
import java.util.List;

public interface BookAuthorMapper {
    List<BookAuthor> getAll();

    BookAuthor getById(String authorId, String bookId);

    List<BookAuthor> getByBookId(String bookId);

    List<BookAuthor> getByAuthorId(String authorId);

    void insert(BookAuthor author);

    void delete(String authorId, String bookId);

    void deleteBookId(String bookId);

    void deleteAuthorId(String authorId);
}
