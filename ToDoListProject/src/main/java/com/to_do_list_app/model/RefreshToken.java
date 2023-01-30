package com.to_do_list_app.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class RefreshToken {


    private int id;
    private int userId;
    private String token;
    private Timestamp createdAt;
    private Timestamp expiredAt;
    private int accessTokenId;


    public RefreshToken() {
    }
}
