package edu.utdallas.cs4347.library.mapper;
import org.apache.ibatis.session.RowBounds;
import java.util.*;
import edu.utdallas.cs4347.library.domain.Book;

public interface BookMapper {
    List<Book> getAll();

    List<Book> getAll(RowBounds rb);

    Book getOneById(String bookId);

    void insert(Book book);

    void update(Book book);

    void delete(String bookId);
}