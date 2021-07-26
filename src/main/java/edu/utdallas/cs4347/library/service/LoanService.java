package edu.utdallas.cs4347.library.service;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.domain.Loan;
import edu.utdallas.cs4347.library.mapper.LoanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

@Service
public class LoanService { 
    private static final Logger log = LogManager.getLogger(LoanService.class);

    @Autowired
    private LoanMapper loanMapper;

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

        for(Loan lo : loans) { 
            if (lo.getDate_in() == null) { 
                throw new BookStateException("This book is already checked out.");
            }
        }
        
        try { 
            loanMapper.insert(l);
        } catch (DataAccessException e) { 
            log.error("Failed to insert loan " + l.toString() + " " + e.getMessage(), e);
            throw new ServiceException("Database critical failure attempting to check out the book, please alert support.");
        }

        return l.getLoan_id();
    }
}