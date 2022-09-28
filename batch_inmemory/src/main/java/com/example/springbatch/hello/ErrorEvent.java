package com.example.springbatch.hello;

public class ErrorEvent {

    private int id;

    private String problemPayload;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProblemPayload() {
        return problemPayload;
    }

    public void setProblemPayload(String problemPayload) {
        this.problemPayload = problemPayload;
    }
}
