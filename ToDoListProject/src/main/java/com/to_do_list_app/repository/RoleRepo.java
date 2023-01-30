package com.to_do_list_app.repository;

import com.to_do_list_app.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Map;

@Repository
public class RoleRepo implements RoleRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Role get(int id) throws SQLException {

        String sql = (Query.roleGet);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        Role role = new Role();

        role.setName((String) result.get("name"));


        Integer roleId = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;
        role.setId(roleId);
        return role;
    }
}
