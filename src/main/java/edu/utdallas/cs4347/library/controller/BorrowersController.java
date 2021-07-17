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
    public LibraryResponse list() {
        LibraryResponse resp = new LibraryResponse();
        List<Borrower> borrowers = null;
        try {
            borrowers = borrowerMapper.getAll();
            resp.setData( borrowers );
        } catch (Exception e) {
            log.error("Exception in list()", e);
            return new LibraryResponse(123, "It's broken, captain!");
        }
        return resp;
    }

    @GetMapping("/{cardNumber}")
    public LibraryResponse getByCard(
            @PathVariable String cardNumber
    ) {
        LibraryResponse resp = new LibraryResponse();
        Borrower borrower = null;
        try {
            borrower = borrowerMapper.getOneByCard( cardNumber );
            resp.setData(borrower);
        } catch (Exception e) {
            log.error("Exception in getByCard()", e);
            return new LibraryResponse(1, "Can't get by card number");
        }
        return resp;
    }

    @PostMapping("/")
    public LibraryResponse add(@RequestBody Borrower b) {
        LibraryResponse resp = new LibraryResponse();
	    borrowerMapper.insert(b);
        log.info("Inserted borrower: " + b.toString());
        resp.setData( borrowerMapper.getOneByCard( b.getCardNumber() ) );
        return resp;
    }

    @PatchMapping("/")
    public LibraryResponse patch(@RequestBody Borrower b) {
        LibraryResponse resp = new LibraryResponse();
        borrowerMapper.update(b);
        log.info("Updated borrower: " + b.toString());
        resp.setData( borrowerMapper.getOneByCard( b.getCardNumber() ) );
        return resp;
    }

    @DeleteMapping("/{cardNumber}")
    public LibraryResponse delete(
            @PathVariable String cardNumber
    ) {
	    LibraryResponse resp = new LibraryResponse();
	    log.info("Deleteing borrower: " + cardNumber);
	    borrowerMapper.delete(cardNumber);
	    return resp;
    }

}