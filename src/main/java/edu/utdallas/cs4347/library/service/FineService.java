package edu.utdallas.cs4347.library.service;

import java.time.Instant;
import java.util.*;
import java.time.temporal.ChronoUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.mapper.*;

@Service
public class FineService { 
    
    private static final Logger log = LogManager.getLogger(FineService.class);

    private static final double DAILY_FINE_RATE = 0.25; // $0.25 per day fine.

    private static final int OVERDUE_THRESHOLD_DAYS = 14; // Books must be returned in 14 days.

    @Autowired
    FineMapper fineMapper;

    @Autowired
    LoanMapper loanMapper;

    @Autowired
    LoanService loanService;

    public List<Fine> getAll() throws ServiceException {
        List<Fine> fines = new ArrayList<Fine>();
        try {
            fines = fineMapper.getAll();
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to query the database: " + e.getMessage());
        }
        fines = attachLoans(fines);
        return fines;
    }

    public Fine getById(String id) throws ServiceException {
        Fine fine;
        try {
            fine = fineMapper.getOneById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to query the database: " + e.getMessage());
        }
        fine = attachLoan(fine);
        return fine;
    }

    public void assess() throws ServiceException {
        List<Fine> fines = null;
        try {
            fines = fineMapper.getUnpaid();
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            throw new ServiceException("Failed to retrieve fines: " + e.getMessage());
        }

        List<Loan> overdueLoans = loanService.getOverdue();

        Date todayDate = new Date();
        Instant today = todayDate.toInstant();
        for(Loan l : overdueLoans) {
            boolean assessedFine = false;
            if (loanService.isOverdue(l)) {

                Date dueDate = l.getDue_date();
                Instant due = dueDate.toInstant();
                long differenceInDays = ChronoUnit.DAYS.between(due, today);

                double newFineAmt = DAILY_FINE_RATE * differenceInDays;
                for(Fine f: fines) {
                    if (f.getLoan_id().equals(l.getLoan_id())) {
                        f.setFine_amt( newFineAmt );
                        fineMapper.update(f);
                        assessedFine = true;
                    }
                }
                if ( !assessedFine ) {
                    Fine f = new Fine();
                    f.setLoan_id(l.getLoan_id());
                    f.setFine_amt( newFineAmt );
                    f.setPaid(false);
                    fineMapper.insert(f);
                }
            }
        }
    }

    public List<Fine> getByBorrower(String borrowerId) throws ServiceException {
        List<Fine> newList = new ArrayList<Fine>();
        try {
            List<Fine> fines = getAll();
            for(Fine f : fines) {
                if (f.getLoan().getCard_id().equals(borrowerId)) {
                    newList.add(f);
                }
            }
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to query the database: " + e.getMessage());
        }
        return newList;
    }

    public Fine attachLoan(Fine f) { 
        f.setLoan(loanService.getLoan(f.getLoan_id())); 
        return f;
    }

    public List<Fine> attachLoans(List<Fine> fines) { 
        List<Fine> newList = new ArrayList<Fine>();
        for(Fine f : fines) { 
            newList.add(attachLoan(f));
        }
        return newList;
    }
}