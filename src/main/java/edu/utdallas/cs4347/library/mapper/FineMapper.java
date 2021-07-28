package edu.utdallas.cs4347.library.mapper;
import java.util.*;
import edu.utdallas.cs4347.library.domain.Fine;

public interface FineMapper {
    List<Fine> getAll();

    Fine getOneById(String loanId);

    void insert(Fine fine);

    void update(Fine fine);

    void delete(String loanId);

    List<Fine> getUnpaid();

}