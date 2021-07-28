package edu.utdallas.cs4347.library.service;

import java.util.*;
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

    private static final double DAILY_FINE_RATE = 0.25;

    @Autowired
    FineMapper fineMapper;

    @Autowired
    LoanMapper loanMapper;

    @Autowired
    LoanService loanService;

    public void assess() throws ServiceException {
        List<Fine> fines = null;
        try {
            fines = fineMapper.getUnpaid();
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            throw new ServiceException("Failed to retrieve fines: " + e.getMessage());
        }

        List<Loan> checkedOutLoans = loanMapper.getCheckedOut();
        
        for(Loan l : checkedOutLoans) {
            boolean assessedFine = false;
            if (loanService.isOverdue(l)) { 
                for(Fine f: fines) { 
                    if (f.getLoan_id() == l.getLoan_id()) { 
                        double newFineAmt = 0.00;
                        if (f.getFine_amt() > 0.00) { 
                            newFineAmt += DAILY_FINE_RATE;
                        } else {
                            newFineAmt = DAILY_FINE_RATE;
                        }
                        f.setFine_amt( newFineAmt );
                        fineMapper.update(f);
                        assessedFine = true;
                    }
                }
                if ( !assessedFine ) { 
                    Fine f = new Fine();
                    f.setLoan_id(l.getLoan_id());
                    f.setFine_amt( DAILY_FINE_RATE );
                    f.setPaid(false);
                    fineMapper.insert(f);
                }
            }
            
        }
    }
}