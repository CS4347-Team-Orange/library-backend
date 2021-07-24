package edu.utdallas.cs4347.library.domain;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String loan_id;
    
    private String book_id;
    private String card_id;
    private Date date_out;
    private Date due_date;
    private Date date_in;

    public String getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public Date getDate_out() {
        return date_out;
    }

    public void setDate_out(Date date_out) {
        this.date_out = date_out;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public Date getDate_in() {
        return date_in;
    }

    public void setDate_in(Date date_in) {
        this.date_in = date_in;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Loan{" +
                "loan_id='" + loan_id + '\'' +
                ", book_id='" + book_id + '\'' +
                ", card_id='" + card_id + '\'' +
                ", date_out=" + date_out +
                ", due_date=" + due_date +
                ", date_in=" + date_in +
                '}';
    }
}