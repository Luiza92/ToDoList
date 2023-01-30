package com.to_do_list_app.service;

import com.to_do_list_app.model.Role;
import com.to_do_list_app.repository.Query;
import com.to_do_list_app.repository.RoleRepo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PermissionService permissionService;


    public Role get(int id) throws SQLException {
        return this.roleRepo.get(id);
    }


    public JSONObject roleGet(int id) throws Exception {

        JSONObject res = new JSONObject();

        String sql = (Query.roleGet);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        if (rows.size() == 0)
            return null;
        Map<String, Object> result = rows.get(0);

        Integer roleId = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;

        JSONArray permissions = this.permissionService.get(roleId);

        res.put("roleName", result.get("name"));
        res.put("roleId", result.get("id"));
        res.put("permissions", permissions);


        return res;
    }

}
