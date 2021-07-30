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
import edu.utdallas.cs4347.library.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

@Service
public class BookService { 
    private static final Logger log = LogManager.getLogger(LoanService.class);

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private LoanService loanService;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private BookAuthorMapper bookAuthorMapper;

    @Autowired
    private BookAuthorService bookAuthorService;

    public Book attachMeta(Book b) {
        b = attachCheckedOut(b);
        b = attachAuthors(b);
        return b;
    }

    public Book attachAuthors(Book b) {
        try {
            List<Author> authors = bookAuthorService.getBookAuthors(b.getBookId());
            b.setAuthors(authors);
        } catch(ServiceException e) {
            log.error("Failed to attach authors",e);
        }
        return b;
    }


    public List<Book> attachAuthors(List<Book> books) {
        List<Book> response = new ArrayList<Book>();
        for(Book b : books) {
           Book newBook = attachAuthors(b);
           response.add(newBook);
        }

        return response;
    }


    public Book attachCheckedOut(Book b) { 
        try { 
            b.setCheckedOut(loanService.isCheckedOut(b.getBookId()));
        } catch (ServiceException e) { 
            log.error("Failed to attach checkout for book: " + b, e);
        }
        return b;
    }

    public List<Book> attachMeta(List<Book> orig) {
        List<Book> listWithMeta = new ArrayList();
        for(Book b : orig) { 
            listWithMeta.add(attachMeta(b));
        }
        return listWithMeta;
    }

    public List<Book> getAll() throws ServiceException {
        List<Book> books = new ArrayList<Book>();
        try {
            books = bookMapper.getAll();
        } catch (DataAccessException e) {
            log.error("Exception in getAll()", e);
            throw new ServiceException("Can't get books " + e.getMessage());
        }
        books = this.attachMeta(books);
        return books;
    }

    public List<Book> getAll(String pageNumber) throws ServiceException {
        PaginatedController pc = new PaginatedController();
        try {
            pc.setByPageNumber(pageNumber);
        } catch(Exception e) {
            log.error(e);
            throw new ServiceException("Failed to paginate result: " + e.getMessage());
        }
        RowBounds rb = pc.getRowBounds();
        List<Book> books = new ArrayList<Book>();
        try {
            books = bookMapper.getAll(rb);
        } catch (DataAccessException e) {
            log.error("Exception in getAll()", e);
            throw new ServiceException("Can't get books " + e.getMessage());
        }
        books = this.attachMeta(books);
        return books;
    }

    public List<Book> getByAuthor(String authorId) throws ServiceException {
        List<Book> result = new ArrayList<Book>();
        log.info("Getting books for author: " + authorId);
        try {
            List<BookAuthor> bas = bookAuthorMapper.getByAuthorId(authorId);

            for (BookAuthor ba : bas) {
                result.add(getOneById(ba.getBookId()));
            }
        } catch (DataAccessException e) {
            log.error("Exception in getAll()", e);
            throw new ServiceException("Can't get books " + e.getMessage());
        } catch (ServiceException e) {
            log.error("Exception in getAll()", e);
            throw new ServiceException("Can't get books " + e.getMessage());
        }
        return result;
    }


    public Book getOneById(String bookId) throws ServiceException{
        Book result = null;
        try {
            result = this.bookMapper.getOneById(bookId);
        } catch (DataAccessException e) {
            log.error("Exception in getById()", e);
            throw new ServiceException("Can't get book " + bookId + ": " + e.getMessage());
        }
        if (result != null) {
            result = this.attachMeta(this.bookMapper.getOneById(bookId));
        }
        return result;
    }

    public Book getOneByIsbn10(String isbn10) throws ServiceException {
        Book foundBook = null;
        try {
            List<Book> result = this.bookMapper.getAll();
            for(Book b : result) {
                if (b.getIsbn10().equals(isbn10)) {
                    foundBook = b;
                    break;
                }
            }
        } catch (DataAccessException e) {
            log.error("Exception in getById()", e);
            throw new ServiceException("Can't get book " + isbn10 + ": " + e.getMessage());
        }
        if (foundBook != null) {
            this.attachMeta(foundBook);
        }
        return foundBook;
    }


    public Book getOneByIsbn13(String isbn13) throws ServiceException {
        Book foundBook = null;
        try {
            List<Book> result = this.bookMapper.getAll();
            for(Book b : result) {
                if (b.getIsbn13().equals(isbn13)) {
                    foundBook = b;
                    break;
                }
            }
        } catch (DataAccessException e) {
            log.error("Exception in getById()", e);
            throw new ServiceException("Can't get book " + isbn13 + ": " + e.getMessage());
        }
        if (foundBook != null) {
            this.attachMeta(foundBook);
        }
        return foundBook;
    }

    public List<Book> search(String query) throws ServiceException { 
        List<Book> result = bookMapper.getAll();
        List<Book> searchResult = new ArrayList<Book>();
        for (Book b : result) {
            if (
                (b.getBookId() != null && b.getBookId().toLowerCase().contains(query.toLowerCase())) ||
                (b.getIsbn10() != null && b.getIsbn10().toLowerCase().contains(query.toLowerCase())) ||
                (b.getIsbn13() != null && b.getIsbn13().toLowerCase().contains(query.toLowerCase())) ||
                (b.getTitle() != null && b.getTitle().toLowerCase().contains(query.toLowerCase())) ||
                (b.getCover() != null && b.getCover().toLowerCase().contains(query.toLowerCase())) ||
                (b.getPublisher() != null && b.getPublisher().toLowerCase().contains(query.toLowerCase()))
            ) { 
                searchResult.add(b);
            }
        }
        searchResult = attachMeta(searchResult);
        return searchResult;
    }

    public void insert(Book b) {
        bookMapper.insert(b);
        bookAuthorService.insertBook(b);
    }

    public void update(Book b) {
        bookMapper.update(b);
    }

    public void delete(String bookId) {
        bookAuthorMapper.deleteBookId(bookId);
        bookMapper.delete(bookId);
    }
}