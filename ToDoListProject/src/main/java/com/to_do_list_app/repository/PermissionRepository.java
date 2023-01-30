package com.to_do_list_app.repository;

import com.to_do_list_app.model.Permission;

import java.sql.SQLException;

public interface PermissionRepository {


    Permission get(int id) throws SQLException;

    int getUserPermission(int userId, String permissionName);
}
