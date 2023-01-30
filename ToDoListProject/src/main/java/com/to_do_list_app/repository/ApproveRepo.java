package com.to_do_list_app.repository;

import com.to_do_list_app.model.Approve;
import com.to_do_list_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Map;

@Repository
public class ApproveRepo implements ApproveRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Approve get(int id) throws SQLException {
        System.err.println("user id " + id);

        String sql = (Query.approveGet);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        Approve approve = new Approve();

      //  approve.setId((int)result.get("id"));
        approve.setUserId((int) result.get("user_id"));
        approve.setRandomId((String) result.get("random_id"));
        approve.setTimeExpires((Timestamp)result.get("time_expires"));


        Integer userId = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;
        approve.setUserId(userId);
        return approve;

    }

//    @Override
//    public int insert(Approve approve) throws SQLException {
//        GeneratedKeyHolder holder = new GeneratedKeyHolder();
//        jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                PreparedStatement statement = con.prepareStatement(Query.approveAdd, Statement.RETURN_GENERATED_KEYS);
//
//                statement.setInt(1, approve.getUser_id());
//                statement.setString(2, approve.getRandomId());
//                statement.setTimestamp(3, approve.getTimeExpires());
//
//                return statement;
//            }
//        }, holder);
//
//      //  return ((Long) holder.getKey().longValue()).intValue();
//        return ((Integer) holder.getKey().intValue()).intValue();
//    }


    @Override
    public int insert(Approve approve) throws SQLException {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(Query.approveAdd, Statement.RETURN_GENERATED_KEYS);


                statement.setInt(1, approve.getUserId());
                statement.setString(2, approve.getRandomId());
                statement.setTimestamp(3, approve.getTimeExpires());



                return statement;
            }
        }, holder);

        return ((Integer) holder.getKey().intValue()).intValue();
    }
    @Override
    public Approve delete(int id) throws SQLException {
//        String sql = Query.a;
//        int result = jdbcTemplate.update(sql, id);
//        if (result == 0) {
//            System.out.println("user has been  delete.");
//            return false;
//        }
//        return true;
        return null;
    }

    @Override
    public int update(Approve approve) throws SQLException {
        String sql = Query.approveUpdate;
        System.err.println(approve.getUserId()+ ", " + approve.getRandomId() + ", " + approve.getTimeExpires()  + ", " + approve.getId());
        int result = jdbcTemplate.update(sql,approve.getUserId(), approve.getRandomId(), approve.getTimeExpires(),  approve.getId());
        if (result > 0) {
            System.out.println("approve has been update.");
            return approve.getId();
        }
        return -1;
    }

    @Override
    public int deleteByUserId(int userId) throws SQLException {

        return jdbcTemplate.update(Query.approveDeleteByUserId,
                userId);

    }

    @Override
    public Approve getByRandomId(String randomId) throws SQLException {

        return jdbcTemplate.query(
                Query.getByRandomId,
                new ResultSetExtractor<Approve>() {
                    @Override
                    public Approve extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {

                            Approve approve = new Approve();

                            approve.setId(rs.getInt("id"));
                            approve.setUserId(rs.getInt("user_id"));
                            approve.setRandomId(rs.getString("random_id"));
                            approve.setTimeExpires(rs.getTimestamp("time_expires"));

                            return approve;
                        }
                        return null;
                    }
                },
                randomId);
    }

}
