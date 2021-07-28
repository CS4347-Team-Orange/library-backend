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

    public void assess() throws ServiceException {
        // Get all the fines
        List<Fine> fines = null;
        try {
            fines = fineMapper.getAll();
        } catch (DataAccessException e) {
            log.error("DataAccessException in list()", e);
            throw new ServiceException("Failed to retrieve fines: " + e.getMessage());
        }

        // Do we need to add any fines to the list?  IE are any loans exceeding their due date, and not in the list?

        // Calculate the fine amounts

        // Persist to the DB
    }
}