package com.to_do_list_app.service;

import com.to_do_list_app.model.Approve;
import com.to_do_list_app.model.User;
import com.to_do_list_app.repository.ApproveRepo;
import com.to_do_list_app.repository.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

@Service
public class ApproveService {


    @Autowired
    ApproveRepo approveRepo;

    @Autowired
    JdbcTemplate jdbcTemplate;


    public int insert(Approve approve) throws SQLException {

        int approveId = this.approveRepo.insert(approve);
        {
            System.err.println("add " + approveId);
        }
        return approveId;
    }


    public Approve get(int id) throws SQLException {

        System.err.println("approve_id" + id);

        String sql = (Query.approveGet);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        Approve approve = new Approve();

        approve.setId((int) result.get("id"));
        approve.setUserId((int) result.get("user_id"));
        approve.setRandomId((String) result.get("random_id"));
        approve.setTimeExpires((Timestamp) result.get("time_expires"));




        approve.setUserId((int) result.get("id"));
        return approve;
    }

    public Approve getByRandomId(String randomId) throws SQLException {
        return this.approveRepo.getByRandomId(randomId);
    }

    public int update(Approve approve) throws SQLException {
        int approveId = this.approveRepo.update(approve);
        {
            System.err.println("add " + approveId);
        }
        return approveId;
    }

    public Approve delete(int id) throws SQLException {
        return this.approveRepo.delete(id);

    }

    public int deleteByUserId(int userId) throws SQLException {
        return this.approveRepo.deleteByUserId(userId);

    }

}
