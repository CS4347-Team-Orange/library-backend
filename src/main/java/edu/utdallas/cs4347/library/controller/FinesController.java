package edu.utdallas.cs4347.library.controller;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jni.Library;
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

import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.mapper.*;
import edu.utdallas.cs4347.library.service.*;
import edu.utdallas.cs4347.library.response.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/fine")
public class FinesController {

    private static final Logger log = LogManager.getLogger(FinesController.class);

    @Autowired
    FineMapper fineMapper;

    @Autowired
    FineService fineService;

    @Autowired
    BookService bookService;

    @GetMapping("/pay/{fineId}/{amount}")
    public LibraryResponse payFine(
        @PathVariable String fineId,
        @PathVariable String amount
    ) {
        try {
            Fine fineToPay = fineService.getById(fineId);

            if (fineToPay.isPaid()) {
                return new LibraryResponse(2, "The fine is already paid.");
            }
            if (Double.parseDouble(amount) != fineToPay.getFine_amt()) {
                return new LibraryResponse(2, "Payment amount must equal fine");
            }
            if (bookService.getOneById(fineToPay.getLoan().getBook_id()).getCheckedOut()) {
                return new LibraryResponse(2, "The book must be checked in before fines can be paid.");
            }
            fineToPay.setPaid(true);
            fineMapper.update(fineToPay);
        } catch (DataAccessException e) {
            log.error("Error trying to pay fine " + fineId, e);
            return new LibraryResponse(1, "failed to pay fine: " + fineId + " | " + e.getMessage());
        } catch (ServiceException e) {
            log.error("Caught fineService error paying fine", e);
            return new LibraryResponse(1, "Internal failure retrieving the fine.");
        }
        return new LibraryResponse();
    }


    @GetMapping("/assess") 
    public LibraryResponse assessFines() { 
        try { 
            fineService.assess();
        } catch (ServiceException e) { 
            return new LibraryResponse(1, "Failed to assess fines: " + e.getMessage());
        }
        return new LibraryResponse();
    }

    @GetMapping("/")
    public LibraryResponse list() {
        LibraryResponse resp = new LibraryResponse();
        List<Fine> fines = null;
        try {
            fines = fineService.getAll();
            resp.setData( fines );
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            return new LibraryResponse(123, "Can't get fines: " + e.getMessage());
        } catch (ServiceException e) {
            log.error("ServiceException trying to get all fines", e);
            return new LibraryResponse(1, "Can't get fines: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/loan/{loan_id}")
    public LibraryResponse getById(
            @PathVariable String loan_id
    ) {
        LibraryResponse resp = new LibraryResponse();
        Fine fine = null;
        try {
            fine = fineMapper.getOneById( loan_id );
            fine = fineService.attachLoan(fine);
            resp.setData(fine);
        } catch (DataAccessException e) {
            log.error("DataAccessException in getById()", e);
            return new LibraryResponse(1, "Can't get by ID, db error: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/borrower/{borrowerId}")
    public LibraryResponse getByBorrower(
            @PathVariable String borrowerId
    ) {
        LibraryResponse resp = new LibraryResponse();
        try {
            List<Fine> fines = fineService.getByBorrower(borrowerId);
            resp.setData(fines);
        } catch(ServiceException e) {
            log.error("Error getting fines", e);
            return new LibraryResponse(1, "Exception occured trying to get fines by borrower: " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/")
    public LibraryResponse add(@RequestBody Fine f) {
        LibraryResponse resp = new LibraryResponse();
        try {
            fineMapper.insert(f);
        } catch(DataAccessException e) {
            log.error("Error adding fine", e);
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }

        log.info("Inserted fine: " + f.toString());
        resp.setData( fineMapper.getOneById( f.getLoan_id() ) );
        return resp;
    }

    @PatchMapping("/")
    public LibraryResponse patch(@RequestBody Fine f) {
        LibraryResponse resp = new LibraryResponse();
        try {
            fineMapper.update(f);
        } catch(DataAccessException e) {
            log.error("Error editing fine", e);
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }
        log.info("Updated fine: " + f.toString());
        resp.setData( fineMapper.getOneById( f.getLoan_id() ) );
        return resp;
    }

    @DeleteMapping("/{loan_id}")
    public LibraryResponse delete(
            @PathVariable String loan_id
    ) {
        LibraryResponse resp = new LibraryResponse();
        log.info("Deleting loan: " + loan_id);
        try {
            fineMapper.delete(loan_id);
        } catch(DataAccessException e) {
            log.error("Error editing fine", e);
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        }
        return resp;
    }

}