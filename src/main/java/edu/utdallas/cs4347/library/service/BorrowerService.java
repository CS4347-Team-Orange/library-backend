package edu.utdallas.cs4347.library.service;

import java.util.*;

//import edu.utdallas.cs4347.library.dao.*;
import edu.utdallas.cs4347.library.domain.*;
import edu.utdallas.cs4347.library.service.*;
import edu.utdallas.cs4347.library.response.LibraryError;

import org.springframework.stereotype.Service;


@Service
public class BorrowerService {

    // @Autowired
    // BorrowerService borrowerDao;

	public List<Borrower> getAll() {
        // try {
        //     List<Borrower> brs = borrowerDao.getAll();
        // } catch (Exception e) { 
        //     throw new Exception("Borrower DAO failed to getAll()");
        // }

        Borrower b1 = new Borrower();
        b1.setPhone("1234567890");

        Borrower b2 = new Borrower();
        b2.setBName("John");

        List<Borrower> brs = new ArrayList<Borrower>();
        brs.add(b1);
        brs.add(b2);
        return brs;
	}




}