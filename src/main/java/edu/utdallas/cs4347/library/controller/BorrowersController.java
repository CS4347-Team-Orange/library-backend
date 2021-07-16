package edu.utdallas.cs4347.library.controller;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.service.*;
import edu.utdallas.cs4347.library.response.LibraryError;

@RestController
@RequestMapping("/api/borrower/")
public class BorrowersController {

    private static final Logger log = LogManager.getLogger(BorrowersController.class);

    @Autowired
    BorrowerService borrowers;

	@GetMapping("/")
	public Object list() {
        List<Borrower> brs = null;
        try {
            brs = borrowers.getAll();
        } catch (Exception e) { 
            log.error("Exception in list()", e);
            return new LibraryError(123, "It's broken, captain!");
        }
        return brs;
	}

    @PostMapping("/")
    public boolean post() { 
        // Send some data to the DB
        return true;
    }



}