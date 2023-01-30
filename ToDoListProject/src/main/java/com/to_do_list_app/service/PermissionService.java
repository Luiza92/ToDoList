package com.to_do_list_app.service;

import com.to_do_list_app.model.Role;
import com.to_do_list_app.repository.PermissionRepo;
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
public class PermissionService {


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PermissionRepo permissionRepo;



    public JSONArray get(int roleId) throws Exception {

        JSONArray permissionArr = new JSONArray();
        String sql = (Query.permissionGet);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, roleId);

        rows.forEach(permission1 -> {

            JSONObject permissionObj = new JSONObject();

            permissionObj.put("permissionName", permission1.get("name"));
            permissionObj.put("enable", permission1.get("enable"));


            permissionArr.put(permissionObj);
        });
        return permissionArr;

    }
}
