package edu.utdallas.cs4347.library.controller;

import java.util.*;

import edu.utdallas.cs4347.library.exception.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.mapper.*;
import edu.utdallas.cs4347.library.service.*;
import edu.utdallas.cs4347.library.response.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/author")
public class AuthorsController {

    private static final Logger log = LogManager.getLogger(AuthorsController.class);

    @Autowired
    AuthorMapper authorMapper;

    @Autowired
    BookAuthorService bookAuthorService;

    @Autowired
    AuthorService authorService;

    @Autowired
    BookAuthorMapper bookAuthorMapper;

    @Autowired
    BookService bookService;

    @GetMapping("/")
    public LibraryResponse list(
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Author> authors = null;
        try {
            authors = authorMapper.getAll();
            resp.setData( authors );
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(123, "Can't get borrowers: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/search/{query}")
    public LibraryResponse search(
            @PathVariable String query,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        if (query.equals("")) {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Search query is required for searching!.");
        }

        try {
            List<Author> searchResult = authorService.search(query);
            resp.setData(searchResult);
        } catch(Exception e) {
            return new LibraryResponse(1, "Exception occured while searching: " + e.getMessage());
        }

        return resp;

    }

    @GetMapping("/{authorId}")
    public LibraryResponse getById(
            @PathVariable String authorId,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        Author author = null;
        try {
            author = authorMapper.getOneById( authorId );
            resp.setData(author);
        } catch (DataAccessException e) {
            log.error("DataAccessException in getOneById()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Can't get by id, db error: " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/")
    public LibraryResponse add(
            @RequestBody Author a,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        try {
            authorMapper.insert(a);
        } catch(DuplicateKeyException e) {
            log.error("Error adding author", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Author already exists");
        } catch(DataAccessException e) {
            log.error("Error adding author", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }

        log.info("Inserted author: " + a.toString());
        resp.setData( authorMapper.getOneById( a.getAuthorId() ) );
        return resp;
    }

    @PatchMapping("/")
    public LibraryResponse patch(
            @RequestBody Author a,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        try {
            authorMapper.update(a);
        } catch(DataAccessException e) {
            log.error("Error editing author", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }
        log.info("Updated author: " + a.toString());
        resp.setData( authorMapper.getOneById( a.getAuthorId() ) );
        return resp;
    }

    @DeleteMapping("/{authorId}")
    public LibraryResponse delete(
            @PathVariable String authorId,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        log.info("Deleting author: " + authorId);
        try {
            bookAuthorMapper.deleteAuthorId(authorId);
            authorMapper.delete(authorId);
        } catch(DataAccessException e) {
            log.error("Error deleting author", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/link/{authorId}/{bookId}")
    public LibraryResponse link(
            @PathVariable String bookId,
            @PathVariable String authorId,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        log.info("Linking author to book: " + authorId + " :: " + bookId);
        try {
            Book b = bookService.getOneById(bookId);
            if (b == null) {
                b = bookService.getOneByIsbn10(bookId);
            }
            if (b == null) {
                b = bookService.getOneByIsbn13(bookId);
            }
            if (b == null) {
                return new LibraryResponse(1, "Can't find book w/ UUID, ISBN10, or ISBN13: " + bookId);
            }
            BookAuthor ba = new BookAuthor();
            ba.setBookId(b.getBookId());
            ba.setAuthorId(authorId);
            bookAuthorMapper.insert(ba);
        } catch(DataAccessException e) {
            log.error("Error linking author", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        } catch (ServiceException e) {
            log.error("Error linking author", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new LibraryResponse(1, "Failed to locate book: " + e.getMessage());
        }
        return resp;
    }
}