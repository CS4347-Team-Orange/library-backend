package edu.utdallas.cs4347.library.repository;

import edu.utdallas.cs4347.library.domain.Borrower;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowerRepository extends CrudRepository<Borrower, String> {

}