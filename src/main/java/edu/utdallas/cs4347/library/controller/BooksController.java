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
import javax.servlet.http.HttpServletResponse;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.mapper.*;
import edu.utdallas.cs4347.library.service.*;
import edu.utdallas.cs4347.library.util.*;
import edu.utdallas.cs4347.library.response.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/book")
public class BooksController {

    private static final Logger log = LogManager.getLogger(BooksController.class);

    @Autowired
    BookMapper bookMapper;

    @Autowired
    BookService bookService;

    @GetMapping("/")
    public LibraryResponse list(
        @RequestParam(name = "page", required = false) String pageNumber,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Book> books = null;
        try {
            if (pageNumber == null) { 
                pageNumber = "1";
            }
            books = bookService.getAll(pageNumber);
            resp.setData( books );
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(123, "Can't get books: " + e.getMessage());
        } catch (ServiceException e) { 
            log.error("ServiceException in list()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Book Service Exception: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/search/{query}")
    public LibraryResponse search(
        @PathVariable String query,
        @RequestParam(name = "page", required = false) String pageNumber,
        HttpServletResponse response
    ) { 
        if (query.equals("")) { 
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Search query is required for searching!.");
        }

        List<Book> result = new ArrayList<Book>();
        try {
            if (pageNumber == null) { 
                pageNumber = "1";
            }
            result = bookService.search(query, pageNumber);
        } catch (ServiceException e) { 
            log.error("Failed serach", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Book service failed to search: " + e.getMessage());
        }
        LibraryResponse resp = new LibraryResponse();
        resp.setData(result);
        return resp;
    }


    @GetMapping("/{bookId}")
    public LibraryResponse getById(
            @PathVariable String bookId,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        Book book = null;
        try {
            book = bookService.getOneById( bookId );
            resp.setData(book);
        } catch (ServiceException e) {
            log.error("Exception in getById()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Can't get book: " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/")
    public LibraryResponse add(
        @RequestBody Book b,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        try { 
            bookMapper.insert(b);
        } catch (DataAccessException e) { 
            log.error("Error inserting", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Error inserting to database: " + e.getMessage());
        }
        
        log.info("Inserted book: " + b.toString());
        resp.setData( bookMapper.getOneById( b.getBookId() ) );
        return resp;
    }

    @PatchMapping("/")
    public LibraryResponse patch(
        @RequestBody Book b,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        try { 
            bookMapper.update(b);
        } catch (DataAccessException e) { 
            log.error("Error patching", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Error updating in database: " + e.getMessage());
        }
        log.info("Updated book: " + b.toString());
        resp.setData( bookMapper.getOneById( b.getBookId() ) );
        return resp;
    }

    @DeleteMapping("/{bookId}")
    public LibraryResponse delete(
            @PathVariable String bookId,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        log.info("Deleting book: " + bookId);
        try { 
            bookMapper.delete(bookId);
        } catch (DataAccessException e) { 
            log.error("Error deleting book", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Error deleting in database: " + e.getMessage()); 
        }
        return resp;
    }

}