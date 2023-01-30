package com.to_do_list_app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permission {


    private int id;
    private String name;
    private int roleId;
    private int enable;


    public Permission() {

    }
}