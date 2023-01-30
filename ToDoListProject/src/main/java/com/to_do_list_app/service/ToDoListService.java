package com.to_do_list_app.service;

import com.to_do_list_app.model.ToDoList;
import com.to_do_list_app.repository.ToDoListRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;
import java.util.Map;

@Service
public class ToDoListService {

    @Autowired
    ToDoListRepo toDoListRepo;

    private final JdbcTemplate jdbcTemplate;

    public ToDoListService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public int add(ToDoList toDoList) throws SQLException {
        ;
        int toDoListId = this.toDoListRepo.insert(toDoList);
        return toDoListId;
    }

    public ToDoList get(int id) throws SQLException {
        return this.toDoListRepo.get(id);
    }

    public ToDoList getByUserId(int userId) throws SQLException {
        return this.toDoListRepo.getByUserId(userId);
    }

    public boolean delete(int id) throws SQLException {
        return this.toDoListRepo.delete(id);

    }

    public boolean deleteBYUserId(int userId) throws SQLException {
        return this.toDoListRepo.deleteByUserId(userId);

    }

    public List<ToDoList> getAllToDoLists(int userId) throws SQLException {
        return this.toDoListRepo.getAllToDoLists(userId);

    }


    public int update(ToDoList toDoList) throws SQLException {
        int toDoListId = this.toDoListRepo.update(toDoList);
        return toDoListId;
    }

    public int getToDoListCount(int userId) throws SQLException {

        String sql = ("SELECT COUNT(*) AS _count FROM to_do_list WHERE user_id = ? ");
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId);

        result.get("_count");

        long count = (long) result.get("_count");

        return (int) count;
    }

}
