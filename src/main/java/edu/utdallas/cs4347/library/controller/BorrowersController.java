package edu.utdallas.cs4347.library.controller;

import java.util.*;

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
import javax.servlet.http.HttpServletResponse;

import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.mapper.*;

import edu.utdallas.cs4347.library.response.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/borrower")
public class BorrowersController {

    private static final Logger log = LogManager.getLogger(BorrowersController.class);

    @Autowired
    BorrowerMapper borrowerMapper;

    @GetMapping("/")
    public LibraryResponse list(
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Borrower> borrowers = null;
        try {
            borrowers = borrowerMapper.getAll();
            resp.setData( borrowers );
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(123, "Can't get borrowers: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/{cardNumber}")
    public LibraryResponse getByCard(
            @PathVariable String cardNumber,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        Borrower borrower = null;
        try {
            borrower = borrowerMapper.getOneByCard( cardNumber );
            resp.setData(borrower);
        } catch (DataAccessException e) {
            log.error("DataAccessException in getByCard()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Can't get by card number, db error: " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/")
    public LibraryResponse add(
        @RequestBody Borrower b,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
	    try { 
            borrowerMapper.insert(b);
        } catch(DataAccessException e) { 
             log.error("Error adding borrower", e);
             response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
             return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }
        
        log.info("Inserted borrower: " + b.toString());
        resp.setData( borrowerMapper.getOneByCard( b.getCardNumber() ) );
        return resp;
    }

    @PatchMapping("/")
    public LibraryResponse patch(
        @RequestBody Borrower b,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        try { 
            borrowerMapper.update(b);
        } catch(DataAccessException e) { 
            log.error("Error editing borrower", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }
        log.info("Updated borrower: " + b.toString());
        resp.setData( borrowerMapper.getOneByCard( b.getCardNumber() ) );
        return resp;
    }

    @DeleteMapping("/{cardNumber}")
    public LibraryResponse delete(
            @PathVariable String cardNumber,
            HttpServletResponse response
    ) {
	    LibraryResponse resp = new LibraryResponse();
	    log.info("Deleteing borrower: " + cardNumber);
	    try { 
            borrowerMapper.delete(cardNumber);
        } catch(DataAccessException e) { 
            log.error("Error editing borrower", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }
        return resp;
    }

}