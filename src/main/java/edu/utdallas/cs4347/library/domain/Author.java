package edu.utdallas.cs4347.library.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String authorId;

    private String name;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Author{" +
                "authorId='" + authorId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
