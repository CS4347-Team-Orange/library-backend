package edu.utdallas.cs4347.library.service;

import java.util.*;

import edu.utdallas.cs4347.library.domain.BookAuthor;
import edu.utdallas.cs4347.library.mapper.AuthorMapper;
import edu.utdallas.cs4347.library.mapper.BookAuthorMapper;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.util.*;
import edu.utdallas.cs4347.library.domain.Book;
import edu.utdallas.cs4347.library.domain.Author;
import edu.utdallas.cs4347.library.domain.BookAuthor;
import edu.utdallas.cs4347.library.mapper.AuthorMapper;
import edu.utdallas.cs4347.library.mapper.BookMapper;
import edu.utdallas.cs4347.library.mapper.BookAuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

@Service
public class BookAuthorService {

    private static final Logger log = LogManager.getLogger(LoanService.class);

    @Autowired
    BookAuthorMapper bookAuthorMapper;

    @Autowired
    AuthorMapper authorMapper;

    public List<Author> getBookAuthors(String bookId) throws ServiceException {
        List<Author> authors = new ArrayList<Author>();

        try {
            List<BookAuthor> bookAuthors = bookAuthorMapper.getByBookId(bookId);
            for (BookAuthor ba : bookAuthors) {
                authors.add(authorMapper.getOneById(ba.getAuthorId()));
            }
        } catch (DataAccessException e) {
            log.error("Error creating authors", e);
            throw e;
        }
        return authors;
    }

    public void insertBook(Book b) {
        List<BookAuthor> bas = new ArrayList<BookAuthor>();
        for(Author a : b.getAuthors()) {
            BookAuthor ba = new BookAuthor();
            ba.setBookId(b.getBookId());
            ba.setAuthorId(a.getAuthorId());
            bookAuthorMapper.insert(ba);
        }
    }

}
