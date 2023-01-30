package com.to_do_list_app.validation;

import java.util.regex.Pattern;

public class UserValidation {




    public boolean isValidUsername(String username) {
        String ePattern = "^([a-z0-9A-Z_-]+)";
        Pattern p = Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(username);
        return m.matches();
    }


    public boolean isValidFirstName(String firstName) {
        String ePattern = "^.*([a-z0-9A-Z_-]+)";
        Pattern p = Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(firstName);
        return m.matches();
    }

    public boolean isValidLastName(String lastName) {
        String ePattern = "^.*([a-z0-9A-Z_-]+)";
        Pattern p = Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(lastName);
        return m.matches();
    }

    public boolean isValidEmail(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,})$";
        Pattern p = Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


    public boolean isValidPassword(String password) {
        String ePattern = "^.*([a-z0-9A-Z_-]+)";
        Pattern p = Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(password);
        return m.matches();
    }


}
