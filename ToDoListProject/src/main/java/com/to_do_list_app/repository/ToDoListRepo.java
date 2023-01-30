package com.to_do_list_app.repository;


import com.to_do_list_app.model.ToDoList;
import com.to_do_list_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ToDoListRepo implements ToDoListRepository{

    private final JdbcTemplate jdbcTemplate;

    public ToDoListRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public ToDoList get(int id) throws SQLException {
        String sql = ("select * from to_do_list where id = ? ");

        // SELECT COUNT(*) FROM to_do_list WHERE user_id = 1



        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        if (rows.size() == 0)
            return null;
        Map<String, Object> result = rows.get(0);

      ToDoList toDoList = new ToDoList();

      //  toDoList.setId((int) result.get("id"));
        toDoList.setTitle((String) result.get("title"));
        toDoList.setDescription((String) result.get("description"));
        toDoList.setCreatedAt((Timestamp) result.get("created_at"));
        toDoList.setExpiredAt((Timestamp) result.get("expired_at"));
        toDoList.setIsComplete((int) result.get("is_complete"));
        toDoList.setUserId((int) result.get("user_id"));


        Integer toDoListId = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;
        toDoList.setId(toDoListId);
        return toDoList;
    }

    @Override
    public int insert(ToDoList toDoList) throws SQLException {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(Query.toDoListAdd, Statement.RETURN_GENERATED_KEYS);


                statement.setInt(1, toDoList.getId());
                statement.setString(2, toDoList.getTitle());
                statement.setString(3, toDoList.getDescription());
                statement.setTimestamp(4, toDoList.getCreatedAt());
                statement.setTimestamp(5, toDoList.getExpiredAt());
                statement.setInt(6, toDoList.getIsComplete());
                statement.setInt(7, toDoList.getUserId());

                return statement;
            }
        }, holder);

        return ((Integer) holder.getKey().intValue()).intValue();
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = (Query.toDoListDelete);
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteByUserId(int userId) throws SQLException {
        String sql = (Query.toDoListDeleteByUserId);
        int result = jdbcTemplate.update(sql, userId);
        if (result == 0) {
            return false;
        }
        return true;
    }

    @Override
    public int update(ToDoList toDoList) throws SQLException {
        String sql = (Query.toDoListUpdate);
        System.err.println(toDoList.getTitle() + ", " + toDoList.getDescription() + ", " + toDoList.getCreatedAt() + ", " + toDoList.getExpiredAt() + ", " + toDoList.getIsComplete() + ", " + toDoList.getUserId() + ", " + toDoList.getId());
        int result = jdbcTemplate.update(sql, toDoList.getTitle() ,toDoList.getDescription(), toDoList.getCreatedAt(), toDoList.getExpiredAt(), toDoList.getIsComplete(), toDoList.getUserId(),  toDoList.getId());

        if (result > 0) {
            return toDoList.getId();
        }
        return -1;
    }

    @Override
    public ToDoList getByUserId(int toDoListId) throws SQLException {
        return jdbcTemplate.query("select * from to_do_list where user_id = ?",
                new ResultSetExtractor<ToDoList>() {
                    @Override
                    public ToDoList extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {
                            ToDoList toDoList = new ToDoList();

                            toDoList.setId(rs.getInt("id"));
                            toDoList.setTitle(rs.getString("title"));
                            toDoList.setDescription(rs.getString("description"));
                            toDoList.setCreatedAt(rs.getTimestamp("created_at"));
                            toDoList.setExpiredAt(rs.getTimestamp("expired_at"));
                            toDoList.setIsComplete(rs.getInt("is_complete"));
                            toDoList.setUserId(rs.getInt("user_id"));

                            return toDoList;
                        }
                        return null;
                    }
                },
                toDoListId);
    }

    @Override
    public List<ToDoList> getAllToDoLists(int userId) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT * FROM to_do_list WHERE user_id = ?", userId);
        List<ToDoList> toDoLists = new ArrayList<>();
        result.forEach(toDoLists1 -> {

            ToDoList toDoList = new ToDoList();

            toDoList.setId((int) toDoLists1.get("id"));
            toDoList.setTitle((String) toDoLists1.get("title"));
            toDoList.setDescription((String) toDoLists1.get("description"));
            toDoList.setCreatedAt((Timestamp) toDoLists1.get("created_at"));
            toDoList.setExpiredAt((Timestamp) toDoLists1.get("expired_at"));
            toDoList.setIsComplete((int) toDoLists1.get("is_complete"));
            toDoList.setUserId((int) toDoLists1.get("user_id"));

            toDoLists.add(toDoList);
        });

        return toDoLists;
    }

}
