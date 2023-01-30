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
public class ToDoList {

    private int id;
    private String title;
    private String description;
    private Timestamp createdAt;
    private Timestamp expiredAt;
    private int isComplete;
    private int userId;




    public JSONObject toJson() {

        JSONObject res = new JSONObject();

        res.put("id", getId());
        res.put("title", getTitle());
        res.put("description", getDescription());
        res.put("createdAt", getCreatedAt());
        res.put("expiredAt", getExpiredAt());
        res.put("isComplete", getIsComplete());
        res.put("userId", getUserId());


        return res;
    }


    public String toJsonString() {
        return toJson().toString();
    }


}
