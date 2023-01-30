package com.to_do_list_app.repository;

import com.to_do_list_app.model.ToDoList;
import com.to_do_list_app.model.User;

import java.sql.SQLException;
import java.util.List;

public interface ToDoListRepository {


    ToDoList get(int id) throws SQLException;

    int insert(ToDoList toDoList) throws SQLException;

    boolean delete(int id) throws SQLException;

    boolean deleteByUserId (int userId) throws SQLException;


    int update(ToDoList toDoList) throws SQLException;

    ToDoList getByUserId(int userId) throws SQLException;
    List<ToDoList> getAllToDoLists(int userId);



}
