package com.cpen391.userapp.dashboardFragments.home;
/**
 * Used for GET requests of /user/me
 */
public class meResult {
    private String email;
    private String id;
    private String firstName;
    private String lastName;
    private String licensePlate;
    private Boolean isAdmin = false;
    private paymentId paymentId;


    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
    public Boolean getAdmin() {
        return isAdmin;
    }

    public Boolean hasPaymentId() {
        return (paymentId!= null);
    }
    public String payment_get_id() {
        return paymentId.get_id();
    }

    public String payment_getCardNum() {
        return paymentId.getCardNum();
    }

    public String payment_getExpDate() {
        return paymentId.getExpDate();
    }

    public String payment_getCvv() {
        return paymentId.getCvv();
    }
}

class paymentId {
    private String _id;
    private String cardNum;
    private String expDate;
    private String cvv;

    public String get_id() {
        return _id;
    }

    public String getCardNum() {
        return cardNum;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getCvv() {
        return cvv;
    }
}
