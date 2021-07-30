package edu.utdallas.cs4347.library.service;

import java.util.*;

import edu.utdallas.cs4347.library.domain.BookAuthor;
import edu.utdallas.cs4347.library.mapper.AuthorMapper;
import edu.utdallas.cs4347.library.mapper.BookAuthorMapper;
import org.apache.ibatis.session.RowBounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.utdallas.cs4347.library.exception.*;
import edu.utdallas.cs4347.library.util.*;
import edu.utdallas.cs4347.library.domain.Book;
import edu.utdallas.cs4347.library.domain.Author;
import edu.utdallas.cs4347.library.domain.BookAuthor;
import edu.utdallas.cs4347.library.mapper.AuthorMapper;
import edu.utdallas.cs4347.library.mapper.BookAuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

@Service
public class AuthorService {

    @Autowired
    AuthorMapper authorMapper;

    public List<Author> search(String query) throws ServiceException {
        List<Author> result = authorMapper.getAll();
        List<Author> searchResult = new ArrayList<Author>();
        for (Author a : result) {
            if (
                    (a.getName() != null && a.getName().toLowerCase().contains(query.toLowerCase()))
            ) {
                searchResult.add(a);
            }
        }
        return searchResult;
    }

}
