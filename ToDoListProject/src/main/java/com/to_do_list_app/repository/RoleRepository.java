package com.to_do_list_app.repository;

import com.to_do_list_app.model.Role;

import java.sql.SQLException;

public interface RoleRepository {


    Role get(int id) throws SQLException;
}
