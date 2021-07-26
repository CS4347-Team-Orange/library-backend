package edu.utdallas.cs4347.library.controller;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.dao.DataAccessException;

import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.mapper.*;
import edu.utdallas.cs4347.library.util.*;
import edu.utdallas.cs4347.library.response.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/book")
public class BooksController {

    private static final Logger log = LogManager.getLogger(BooksController.class);

    @Autowired
    BookMapper bookMapper;

    @GetMapping("/")
    public LibraryResponse list(@RequestParam(name = "page", required = false) String pageNumber) {
        PaginatedController pc = new PaginatedController();
        try { 
            pc.setByPageNumber(pageNumber);
        } catch(Exception e) { 
            log.error(e);
            return new LibraryResponse(123, "Pagination controller exploded: " + e.getMessage());
        }
        RowBounds rb = pc.getRowBounds();
        LibraryResponse resp = new LibraryResponse();
        List<Book> books = null;
        try {
            books = bookMapper.getAll(rb);
            resp.setData( books );
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            return new LibraryResponse(123, "Can't get books: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/{bookId}")
    public LibraryResponse getById(
            @PathVariable String bookId
    ) {
        LibraryResponse resp = new LibraryResponse();
        Book book = null;
        try {
            book = bookMapper.getOneById( bookId );
            resp.setData(book);
        } catch (DataAccessException e) {
            log.error("Exception in getById()", e);
            return new LibraryResponse(1, "Can't get book: " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/")
    public LibraryResponse add(@RequestBody Book b) {
        LibraryResponse resp = new LibraryResponse();
        try { 
            bookMapper.insert(b);
        } catch (DataAccessException e) { 
            log.error("Error inserting", e);
            return new LibraryResponse(1, "Error inserting to database: " + e.getMessage());
        }
        
        log.info("Inserted book: " + b.toString());
        resp.setData( bookMapper.getOneById( b.getBookId() ) );
        return resp;
    }

    @PatchMapping("/")
    public LibraryResponse patch(@RequestBody Book b) {
        LibraryResponse resp = new LibraryResponse();
        try { 
            bookMapper.update(b);
        } catch (DataAccessException e) { 
            log.error("Error patching", e);
            return new LibraryResponse(1, "Error updating in database: " + e.getMessage());
        }
        log.info("Updated book: " + b.toString());
        resp.setData( bookMapper.getOneById( b.getBookId() ) );
        return resp;
    }

    @DeleteMapping("/{bookId}")
    public LibraryResponse delete(
            @PathVariable String bookId
    ) {
        LibraryResponse resp = new LibraryResponse();
        log.info("Deleting book: " + bookId);
        try { 
            bookMapper.delete(bookId);
        } catch (DataAccessException e) { 
            log.error("Error deleting book", e);
            return new LibraryResponse(1, "Error deleting in database: " + e.getMessage()); 
        }
        return resp;
    }

}