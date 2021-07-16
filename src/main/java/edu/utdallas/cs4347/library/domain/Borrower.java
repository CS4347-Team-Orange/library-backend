package edu.utdallas.cs4347.library.domain;


public class Borrower { 

    private String cardNumber;

    private String address;

    private String phone;

    private String Bname;

    private String ssn;

    public Borrower() { 

    }

    public void setCardNumber(String cn) { 
        this.cardNumber = cn;
    }

    public void setAddress(String ad) { 
        this.address = ad;
    }

    public void setPhone(String ph) { 
        this.phone = ph;
    }

    public void setBName(String name) { 
        this.Bname = name;
    }

    public void setSsn(String ssn) { 
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

    public String getBName() { 
        return this.Bname;
    }

    public String getSsn() {
        return this.ssn;
    }

}