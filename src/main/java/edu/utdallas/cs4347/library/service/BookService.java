package edu.utdallas.cs4347.library.service;

import java.util.*;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.util.*;
import edu.utdallas.cs4347.library.domain.Book;
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

    public Book attachCheckedOut(Book b) { 
        try { 
            b.setCheckedOut(loanService.isCheckedOut(b.getBookId()));
        } catch (ServiceException e) { 
            log.error("Failed to attach checkout for book: " + b, e);
        }
        return b;
    }

    public List<Book> attachCheckedOut(List<Book> orig) { 
        List<Book> listWithCheckedOut = new ArrayList();
        for(Book b : orig) { 
            listWithCheckedOut.add(attachCheckedOut(b));
        }
        return listWithCheckedOut;
    }

    public List<Book> getAll() throws ServiceException {
        List<Book> books = new ArrayList<Book>();
        try {
            books = bookMapper.getAll();
        } catch (DataAccessException e) {
            log.error("Exception in getAll()", e);
            throw new ServiceException("Can't get books " + e.getMessage());
        }
        books = this.attachCheckedOut(books);
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
        books = this.attachCheckedOut(books);
        return books;
    }

    public Book getOneById(String bookId) throws ServiceException{
        try { 
            Book result = this.bookMapper.getOneById(bookId);
        } catch (DataAccessException e) {
            log.error("Exception in getById()", e);
            throw new ServiceException("Can't get book " + bookId + ": " + e.getMessage());
        } 
        return this.attachCheckedOut(this.bookMapper.getOneById(bookId));
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
        if (foundBook == null) {
            throw new ServiceException("Can't locate book w/ that ISBN10");
        }
        return this.attachCheckedOut(foundBook);
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
        if (foundBook == null) {
            throw new ServiceException("Can't locate book w/ that ISBN10");
        }
        return this.attachCheckedOut(foundBook);
    }

    public List<Book> search(String query) throws ServiceException { 
        List<Book> result = bookMapper.getAll();
        List<Book> searchResult = new ArrayList<Book>();
        for (Book b : result) { 
            log.info(b);
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
        searchResult = attachCheckedOut(searchResult);
        return searchResult;
    }
}