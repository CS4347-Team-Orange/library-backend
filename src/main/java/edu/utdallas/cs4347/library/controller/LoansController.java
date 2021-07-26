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


import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.mapper.*;
import edu.utdallas.cs4347.library.util.*;
import edu.utdallas.cs4347.library.service.*;
import edu.utdallas.cs4347.library.response.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/loan")
public class LoansController {

    private static final Logger log = LogManager.getLogger(LoansController.class);

    @Autowired
    LoanMapper loanMapper;

    @Autowired
    LoanService loanService;

    @GetMapping("/")
    public LibraryResponse list() {
        LibraryResponse resp = new LibraryResponse();
        List<Loan> loans = null;
        try {
            loans = loanMapper.getAll();
            resp.setData( loans );
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            return new LibraryResponse(123, "Can't get loans: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/book/{bookId}")
    public LibraryResponse getByBook(
            @PathVariable String bookId
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Loan> loans = null;
        try {
            loans = loanMapper.getByBook( bookId );
            resp.setData(loans);
        } catch (DataAccessException e) {
            log.error("DataAccessException in getByBook()", e);
            return new LibraryResponse(1, "Can't get by book, db error: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/card/{cardNumber}")
    public LibraryResponse getByCard(
            @PathVariable String cardNumber
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Loan> loans = null;
        try {
            loans = loanMapper.getByCard( cardNumber );
            resp.setData(loans);
        } catch (DataAccessException e) {
            log.error("DataAccessException in getByCard()", e);
            return new LibraryResponse(1, "Can't get by card number, db error: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/checkOut/{bookId}/{cardId}")
    public LibraryResponse add(@PathVariable String cardId, @PathVariable String bookId) {
        LibraryResponse resp = new LibraryResponse();
        String loanId = "";
        try {
            loanId = loanService.checkout(bookId, cardId);
        } catch(DataAccessException e) {
            log.error("Error checking out book", e);
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        } catch(BookStateException e) { 
            return new LibraryResponse(1, "Failed to check out book.  It's already checked out.");
        } catch (ServiceException e) { 
            return new LibraryResponse(1, e.getMessage());
        } catch (Exception e) { 
            return new LibraryResponse(1, "Something went horribly wrong trying to check the book out.");
        }

        log.info("Checked out book: " + bookId + "\n" + cardId);
        resp.setData( loanMapper.getById( loanId ) );
        return resp;
    }

    @GetMapping("/checkIn/{loanId}")
    public LibraryResponse checkIn(@PathVariable String loanId) {
        LibraryResponse resp = new LibraryResponse();
        try {
            Loan l = loanMapper.getById(loanId);
            l.setDate_in(new Date());
            loanMapper.update(l);
        } catch (DataAccessException e) {
            log.error("Error checking in book", e);
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }

        log.info("Checked in book: " + loanId);
        return resp;
    }
}