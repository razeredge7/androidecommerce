package com.crew.foundation;

import com.facebook.share.widget.ShareButton;

public class PaymentView {
    String username;
    String email;
    String trans_id;
    String cost;
    String method;
    String trans_date;

    public PaymentView() {

    }

    public PaymentView(String username, String email, String trans_id, String cost, String method,String trans_date) {
        this.username = username;
        this.email = email;
        this.trans_id = trans_id;
        this.cost = cost;
        this.method = method;
        this.trans_date = trans_date;
    }

    public String getTrans_date() {
        return trans_date;
    }

    public void setTrans_date(String trans_date) {
        this.trans_date = trans_date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
