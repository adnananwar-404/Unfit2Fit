package com.unfit2fit.unfit2fit;

public class User {

    private String username, full_name, email_address, password, user_id;

    public User(String uID, String user, String name, String email, String pass){
        this.full_name = name;
        this.username = user;
        this.email_address = email;
        this.password = pass;
        this.user_id = uID;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public String getPassword() {
        return password;
    }

    public String getUser_id() {
        return user_id;
    }
}
