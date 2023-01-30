package com.to_do_list_app.repository;

import com.to_do_list_app.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Map;

@Repository
public class PermissionRepo implements PermissionRepository {

   @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Permission get(int id) throws SQLException {

        String sql = (Query.permissionGet);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        Permission permission = new Permission();

        permission.setName((String) result.get("name"));
        permission.setRoleId((int) result.get("role_id"));
        permission.setEnable((int) result.get("enable"));

        Integer permissionId = result.get("id") != null ? ((Long) result.get("id")).intValue() : null;
        permission.setId(permissionId);
        return permission;
    }


    @Override
    public int getUserPermission(int userId, String permissionName) {
        try {
            String sql = (Query.getUserPermission);

            Map<String, Object> res = jdbcTemplate.queryForMap(sql, userId, permissionName);
            return (int) res.get("enable");

        } catch (Exception ex) {
            return 0;
        }

    }
}
