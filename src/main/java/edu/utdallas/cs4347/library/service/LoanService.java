package edu.utdallas.cs4347.library.service;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.mapper.*;
import edu.utdallas.cs4347.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

@Service
public class LoanService { 
    private static final Logger log = LogManager.getLogger(LoanService.class);

    private static final int MAX_ACTIVE_LOANS = 3;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private BorrowerMapper borrowerMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowerService borrowerService;

    public Loan getLoan(String loanId) {
        Loan l = loanMapper.getById(loanId);
        l = attachBorrower(l);
        l = attachBook(l);
        return l;
    }

    public boolean isOverdue(Loan l) { 
        Date today = new Date();

        if (l != null && today.after(l.getDue_date()) && l.getDate_in() == null) {
            return true;
        }

        return false;
    }

    public List<Loan> getOverdue() throws ServiceException {
        List<Loan> loans = new ArrayList<Loan>();

        try { 
            loans = loanMapper.getOverdue();
        } catch (Exception e) { 
            log.error("Error getting loans from database!", e);
            throw new ServiceException(e.getMessage());
        }

        loans = attachBook(loans);
        loans = attachBorrower(loans);
        return loans;
    }

    public boolean isCheckedOut(String bookId) throws ServiceException { 
        List<Loan> loans = new ArrayList<Loan>();

        try { 
            loans = loanMapper.getByBook(bookId);
        } catch (Exception e) { 
            log.error("Error getting book from database!", e);
            throw new ServiceException(e.getMessage());
        }

        for(Loan l : loans) { 
            if (l.getDate_in() == null) { 
                return true;
            }
        }
        return false;
    }

    public Loan attachBook(Loan l) { 
        l.setBook(bookMapper.getOneById(l.getBook_id())); 
        return l;
    }

    public Loan attachBorrower(Loan l) { 
        l.setBorrower(borrowerMapper.getOneByCard(l.getCard_id()));
        return l;
    }

    public List<Loan> attachBorrower(List<Loan> loans) { 
        List<Loan> newList = new ArrayList<Loan>();
        for(Loan l: loans) { 
            l.setBorrower(borrowerMapper.getOneByCard(l.getCard_id()));
            newList.add(l);
        }
        return newList;
    }

    public List<Loan> attachBook(List<Loan> loans) { 
        List<Loan> newList = new ArrayList<Loan>();
        for (Loan l : loans) {
            l.setBook(bookMapper.getOneById(l.getBook_id())); 
            newList.add(l);
        }
        return newList;
    }

    public String checkout(String bookId, String cardNumber) throws Exception { 
        Loan l = new Loan();
        l.setCard_id(cardNumber);
        l.setBook_id(bookId);
        List<Loan> loans = new ArrayList<Loan>();

        try { 
            loans = loanMapper.getByBook(bookId);
        } catch (Exception e) { 
            log.error("Error!", e);
            throw new Exception(e);
        }

        if (this.isCheckedOut(bookId)) {
            throw new BookStateException("This book is already checked out.");
        }
        
        if (! borrowerService.cardExists(cardNumber)) { 
            throw new ServiceException("The Card Number is invalid");
        }

        if (totalActiveLoansForBorrower(cardNumber) >= MAX_ACTIVE_LOANS) { 
            throw new ServiceException("Borrower has 3 active loans already, cannot loan more books.");
        }

        try { 
            loanMapper.insert(l);
        } catch (DataAccessException e) { 
            log.error("Failed to insert loan " + l.toString() + " " + e.getMessage(), e);
            throw new ServiceException("Database critical failure attempting to check out the book, please alert support.");
        }

        return l.getLoan_id();
    }

    public void checkin(String bookId) throws ServiceException { 
        if (! isCheckedOut(bookId)) { 
            throw new ServiceException("Book is not checked out!");
        }
        Loan l = findActiveLoan(bookId);
        l.setDate_in(new Date());
        log.info("Checking in: " + l);
        loanMapper.update(l);
    }

    public Loan findActiveLoan(String bookId) { 
        List<Loan> loans = loanMapper.getAll();
        for(Loan l: loans) { 
            if (l.getDate_in() == null) { 
                return l;
            }
        }
        return null;
    }

    public int totalActiveLoansForBorrower(String cardId) throws ServiceException { 
        Borrower b = borrowerMapper.getOneByCard(cardId);
        if (b == null) { 
            throw new ServiceException("Borrower does not exist.");
        }

        List<Loan> loans = loanMapper.getByCard(cardId);
        int activeLoans = 0;
        
        for(Loan l : loans) { 
            if (l.getDate_in() == null) { 
                activeLoans++;
            }
        }
        log.info("Borrower " + cardId + " has active loans: " + activeLoans); 
        return activeLoans;
    }

    public List<Loan> search(String query) throws ServiceException { 
        // Search borrower names, book ids, and card id's
        List<Loan> result = loanMapper.getAll();
        result = attachBook(result);
        result = attachBorrower(result);
        List<Loan> searchResult = new ArrayList<Loan>();
        for(Loan l : result) {
            if (
                (l.getBook().getBookId().toLowerCase().contains(query.toLowerCase())) ||
                (l.getBorrower().getCardNumber().toLowerCase().contains(query.toLowerCase())) ||
                (l.getBorrower().getFirstName().toLowerCase().contains(query.toLowerCase())) ||
                (l.getBorrower().getLastName().toLowerCase().contains(query.toLowerCase())) ||
                (l.getBook().getTitle().toLowerCase().contains(query.toLowerCase())) ||
                (l.getBook().getIsbn10().toLowerCase().contains(query.toLowerCase())) ||
                (l.getBook().getIsbn13().toLowerCase().contains(query.toLowerCase()))
            ) { 
                searchResult.add(l);
            }
        }
        return searchResult;
    }

}