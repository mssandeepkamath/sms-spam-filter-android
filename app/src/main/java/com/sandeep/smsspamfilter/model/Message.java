package com.sandeep.smsspamfilter.model;

public class Message {
    private String body;
    private String address;

    private int result;

    public Message(String body, String address, int result) {
        this.body = body;
        this.address = address;
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
