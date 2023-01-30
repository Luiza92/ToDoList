package com.to_do_list_app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int imageId;
    private int roleId;
    private int status;
    private String confirmPassword;


    private JSONObject role;
    private JSONObject image;



    public User(User modelTO,Timestamp TimeExpiresDateExDate,JSONObject role, JSONObject image) {

        setId(modelTO.getId());
        setUsername(modelTO.getUsername());
        setFirstName(modelTO.getFirstName());
        setLastName(modelTO.getLastName());
        setEmail(modelTO.getEmail());
        setPassword(new BCryptPasswordEncoder().encode(modelTO.getPassword()));
        setUpdatedAt(TimeExpiresDateExDate);
        setCreatedAt(TimeExpiresDateExDate);
        setImageId(modelTO.getImageId());
        setRoleId(modelTO.getRoleId());
        setStatus(modelTO.getStatus());


        setImage(image);
        setRole(role);

    }

    public User(User modelTO,Timestamp TimeExpiresDateExDate,JSONObject image) {

        setId(modelTO.getId());
        setUsername(modelTO.getUsername());
        setFirstName(modelTO.getFirstName());
        setLastName(modelTO.getLastName());
        setEmail(modelTO.getEmail());
        setPassword(new BCryptPasswordEncoder().encode(modelTO.getPassword()));
        setUpdatedAt(TimeExpiresDateExDate);
        setCreatedAt(TimeExpiresDateExDate);
        setImageId(modelTO.getImageId());
        setStatus(modelTO.getStatus());


        setImage(image);


    }

    public JSONObject toJson() {

        JSONObject res = new JSONObject();

        res.put("id", getId());
        res.put("username", getUsername());
        res.put("firstName", getFirstName());
        res.put("lastName", getLastName());
        res.put("email", getEmail());
        res.put("createdAt", getCreatedAt());
        res.put("updatedAt", getUpdatedAt());
        res.put("image", getImage());
        res.put("role", getRole());
        res.put("status", getStatus());

        return res;
    }


    public String toJsonString() {
        return toJson().toString();
    }



    public void setRole(Role role) {
        this.role = new JSONObject();
        this.role.put("id", role.getId());
        this.role.put("name", role.getName());
        System.err.println("this.role "+this.role);
    }
    public void setRole(JSONObject role) {
        this.role = role;
    }


}
