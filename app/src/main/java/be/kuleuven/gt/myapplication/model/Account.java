package be.kuleuven.gt.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class Account {

    //instantiations

    private String username;
    private String password;

    //Constructors, HashMap and getters
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Map<String, String> getPostParameters() {
        Map<String,String> params = new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        return params;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}