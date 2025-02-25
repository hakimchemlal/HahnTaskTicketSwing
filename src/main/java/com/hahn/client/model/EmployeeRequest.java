package com.hahn.client.model;

public class EmployeeRequest {
    private String fullName;
    private String username;

    public EmployeeRequest() {
    }

    public EmployeeRequest(String username) {
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }




    @Override
    public String toString() {
        return "EmployeeRequest{" +
                "fullName='" + fullName +
                '}';
    }
}
