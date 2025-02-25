package com.hahn.client.model;

import java.util.List;

public class AuthenticatedUserResponse {


    private String username;
    private String fullName;
    private List<String> authorities;

    public AuthenticatedUserResponse() {
    }

    public AuthenticatedUserResponse(String username, String fullName, List<String> authorities) {
        this.username = username;
        this.fullName = fullName;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }


}
