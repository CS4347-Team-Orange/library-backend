package edu.utdallas.cs4347.library.mapper;

import edu.utdallas.cs4347.library.domain.Author;
import java.util.List;

public interface AuthorMapper {
    List<Author> getAll();

    Author getOneById(String authorId);

    void insert(Author author);

    void update(Author author);

    void delete(String authorId);
}
