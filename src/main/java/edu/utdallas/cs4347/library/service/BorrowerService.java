package edu.utdallas.cs4347.library.service;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.domain.Borrower;
import edu.utdallas.cs4347.library.mapper.BorrowerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

@Service
public class BorrowerService { 
    private static final Logger log = LogManager.getLogger(BorrowerService.class);

    @Autowired
    private BorrowerMapper borrowerMapper;

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