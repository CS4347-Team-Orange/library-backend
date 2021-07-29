package edu.utdallas.cs4347.library.domain;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
public class Fine {

    public String getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public double getFine_amt() {
        return fine_amt;
    }

    public void setFine_amt(double fine_amt) {
        this.fine_amt = fine_amt;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Fine{" +
                "loan_id='" + loan_id + '\'' +
                ", fine_amt=" + fine_amt +
                ", paid=" + paid +
                ", loan=" + loan +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String loan_id;
    private double fine_amt;
    private boolean paid;

    @Transient
    private Loan loan;

    }