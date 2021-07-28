package edu.utdallas.cs4347.library.mapper;
import java.util.*;
import edu.utdallas.cs4347.library.domain.Loan;

public interface LoanMapper {
    List<Loan> getAll();
    List<Loan> getByBook(String bookId);
    List<Loan> getByCard(String cardNumber);
    List<Loan> getCheckedOut();
    List<Loan> getOverdue();
    Loan getById(String loanId);
    
    void insert(Loan loan);
    void update(Loan loan);

}