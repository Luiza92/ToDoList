package com.to_do_list_app.model;


import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Approve {

    private int id;
    private int userId;
    private String randomId;
    private Timestamp timeExpires;


    public Approve() {
    }
}