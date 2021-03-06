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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.dao.DataAccessException;
import javax.servlet.http.HttpServletResponse;

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
    BookService bookService;

    @Autowired
    BorrowerMapper borrowerMapper;

    @Autowired
    LoanMapper loanMapper;

    @Autowired
    LoanService loanService;

    @GetMapping("/")
    public LibraryResponse list(
        @RequestParam(name = "query", required = false) String query,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Loan> loans = null;
        try {
            if (query == null) { 
                loans = loanMapper.getAll();
            } else { 
                loans = loanService.search(query);
            }
            loans = loanService.attachBook(loans);
            loans = loanService.attachBorrower(loans);
            resp.setData( loans );
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(123, "Can't get loans: " + e.getMessage());
        } catch (ServiceException e) { 
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(123, "Can't get loans: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/book/{bookId}")
    public LibraryResponse getByBook(
            @PathVariable String bookId,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Loan> loans = null;
        try {
            loans = loanMapper.getByBook( bookId );
            loans = loanService.attachBook(loans);
            loans = loanService.attachBorrower(loans);
            Collections.reverse(loans);
            resp.setData(loans);
        } catch (DataAccessException e) {
            log.error("DataAccessException in getByBook()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Can't get by book, db error: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/card/{cardNumber}")
    public LibraryResponse getByCard(
            @PathVariable String cardNumber,
            HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        List<Loan> loans = null;
        try {
            loans = loanMapper.getByCard( cardNumber );
            loans = loanService.attachBook(loans);
            loans = loanService.attachBorrower(loans);
            Collections.reverse(loans);
            resp.setData(loans);
        } catch (DataAccessException e) {
            log.error("DataAccessException in getByCard()", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Can't get by card number, db error: " + e.getMessage());
        }
        return resp;
    }

    @GetMapping("/checkOut/{bookId}/{cardId}")
    public LibraryResponse add(
        @PathVariable String cardId, 
        @PathVariable String bookId,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        String loanId = "";
        try {
            loanId = loanService.checkout(bookId, cardId);
        } catch(DataAccessException e) {
            log.error("Error checking out book", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by database engine: " + e.getMessage());
        } catch(BookStateException e) { 
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Failed to check out book.  It's already checked out.");
        } catch (ServiceException e) { 
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, e.getMessage());
        } catch (Exception e) { 
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Something went horribly wrong trying to check the book out.");
        }

        log.info("Checked out book: " + bookId + "\n" + cardId);
        resp.setData( loanMapper.getById( loanId ) );
        return resp;
    }

    @GetMapping("/checkIn/book/{bookId}")
    public LibraryResponse checkInWithBookid(
        @PathVariable String bookId,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        try {
            loanService.checkin(bookId); 
        } catch (ServiceException e) {
            log.error("Error checking in book", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by LoanService: " + e.getMessage());
        }

        log.info("Checked in book: " + bookId);
        return resp;
    }

    @GetMapping("/checkIn/loan/{loanId}")
    public LibraryResponse checkIn(
        @PathVariable String loanId,
        HttpServletResponse response
    ) {
        LibraryResponse resp = new LibraryResponse();
        try {
            Loan l = loanMapper.getById(loanId);
            loanService.checkin(l.getBook_id()); 
        } catch (ServiceException e) {
            log.error("Error checking in book", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned by LoanService: " + e.getMessage());
        }

        log.info("Checked in book via loan: " + loanId);
        return resp;
    }

    @GetMapping("/seed")
    public LibraryResponse seed(
        HttpServletResponse response
    ) {
        log.info("Seeding...");
        int borrowersCount = 50;
        LibraryResponse resp = new LibraryResponse();
        List<Loan> seeds = new ArrayList<Loan>();
        List<String> cardIDs = new ArrayList<String>();
        List<String> bookIDs = new ArrayList<String>();
        try {
            List<Borrower> borrowers = borrowerMapper.getAll();
            List<Book> books = bookService.getAll();
            Random rand = new Random();
            for(int i = 0; i < borrowersCount; i++) {
                cardIDs.add(borrowers.get(rand.nextInt(borrowers.size())).getCardNumber());
            }
            log.info("Selected " + cardIDs.size() + " Card IDs: " + cardIDs);

            boolean addingBooks = true;
            int booksAdded = 0;
            int booksToAdd = 100;
            while (addingBooks) {
                Book b = books.get(rand.nextInt(books.size()));
                if (! b.getCheckedOut() && ! bookIDs.contains(b.getBookId())) {
                    bookIDs.add(b.getBookId());
                    booksAdded++;
                }
                if (booksAdded == booksToAdd) {
                    addingBooks = false;
                }
            }
            log.info("Selected " + booksToAdd + " books.");
            Calendar c = new GregorianCalendar();
            c.add(Calendar.DAY_OF_MONTH, -19);
            Date NINETEEN_DAYS_AGO = c.getTime();

            Calendar c2 = new GregorianCalendar();
            c2.add(Calendar.DAY_OF_MONTH, -5);
            Date FIVE_DAYS_AGO = c2.getTime();


            log.info("Seeding loans...");
            for(int i = 0; i < booksToAdd; i++) {
                int borrower = i % borrowersCount;
                Loan l = new Loan();
                l.setBook_id(bookIDs.get(i));
                l.setCard_id(cardIDs.get(borrower));
                loanMapper.insert(l);
                l.setDate_out(NINETEEN_DAYS_AGO);
                l.setDue_date(FIVE_DAYS_AGO);
                loanMapper.update(l);
                seeds.add(l);
            }
            log.info("Loan seeding completed.");
        } catch (Exception e) {
            log.error("Error seeding loans", e);
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return new LibraryResponse(1, "Exception returned seeding data: " + e.getMessage());
        }

        log.info("Seeded loans " + seeds);
        return resp;
    }
}