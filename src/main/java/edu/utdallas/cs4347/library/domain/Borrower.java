package edu.utdallas.cs4347.library.domain;

import java.util.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Borrower {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String cardNumber;

    private String address;

    private String phone;

    private String name;

    private Long ssn;

    public void setCardNumber(String cn) {
        this.cardNumber = cn;
    }

    public void setAddress(String ad) { 
        this.address = ad;
    }

    public void setPhone(String ph) { 
        this.phone = ph;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSsn(Long ssn) {
        this.ssn = ssn;
    }


    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getAddress() { 
        return this.address;
    }

    public String getPhone() { 
        return this.phone;
    }

    public String getName() {
        return this.name;
    }

    public Long getSsn() {
        return this.ssn;
    }

    public String toString() { 
        return "Borrower{" + 
        "name='" + name +  '\'' +
        "ssn='" + ssn +  '\'' +
        "phone='" + phone +  '\'' +
        "address='" + address +  '\'' +
        "cardNumber='" + cardNumber + '}';
             
    }

}