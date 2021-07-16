package edu.utdallas.cs4347.library.mapper;
import java.util.*;
import edu.utdallas.cs4347.library.domain.Borrower;

public interface BorrowerMapper {
    List<Borrower> getAll();

    Borrower getOneBySsn(int ssn);

    Borrower getOneByCard(String cardId);

    void insert(Borrower borrower);

    void update(Borrower borrower);

    void delete(String cardId);
}