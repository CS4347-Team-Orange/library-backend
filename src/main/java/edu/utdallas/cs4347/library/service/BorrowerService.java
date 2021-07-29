package edu.utdallas.cs4347.library.service;

import java.util.*;

import edu.utdallas.cs4347.library.response.LibraryResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.domain.Borrower;
import edu.utdallas.cs4347.library.mapper.BorrowerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import javax.servlet.http.HttpServletResponse;

@Service
public class BorrowerService { 
    private static final Logger log = LogManager.getLogger(BorrowerService.class);

    @Autowired
    private BorrowerMapper borrowerMapper;

    public List<Borrower> search(String query) throws ServiceException {

        List<Borrower> all = borrowerMapper.getAll();
        List<Borrower> searchResult = new ArrayList<Borrower>();
        for(String word : Arrays.asList(query.split(" "))) {
            try {
                for(Borrower bo : all) {
                    if (
                        (bo.getFullName() != null && bo.getFullName().toLowerCase().contains(word.toLowerCase())) ||
                        (bo.getCardNumber() != null && bo.getCardNumber().toLowerCase().contains(word.toLowerCase())) ||
                        (bo.getEmail() != null && bo.getEmail().toLowerCase().contains(word.toLowerCase())) ||
                        (bo.getAddress() != null && bo.getAddress().toLowerCase().contains(word.toLowerCase())) ||
                        (bo.getState() != null && bo.getState().toLowerCase().contains(word.toLowerCase())) ||
                        (bo.getCity() != null && bo.getCity().toLowerCase().contains(word.toLowerCase())) ||
                        (bo.getPhone() != null && bo.getPhone().toLowerCase().contains(word.toLowerCase())) ||
                        (bo.getSsn() != null && bo.getSsn().toLowerCase().contains(word.toLowerCase()))
                    ) {
                        searchResult.add(bo);
                    }
                }
            } catch (DataAccessException e) {
                log.error("Failed to search",e);
                throw new ServiceException("Failed to search borrowers: " + e.getMessage());
            }
        }
        return searchResult;
    }

    public boolean missingRequiredField(Borrower b) {
        if (
            b.getFirstName() == null || b.getFirstName().equals("") ||
            b.getLastName() == null || b.getLastName().equals("") ||
            b.getAddress() == null || b.getAddress().equals("") ||
            b.getPhone() == null || b.getPhone().equals("") ||
            b.getSsn() == null || b.getSsn().equals("") ||
            b.getEmail() == null || b.getEmail().equals("") ||
            b.getCity() == null || b.getCity().equals("") ||
            b.getState() == null || b.getState().equals("")
        ) { 
            return true;
        } else {
            return false;
        }
    }

    public boolean cardExists(String cardNumber) { 
        Borrower b = borrowerMapper.getOneByCard(cardNumber);
        if (b == null) {
            return false;
        } else {
            return true;
        }
    }
}