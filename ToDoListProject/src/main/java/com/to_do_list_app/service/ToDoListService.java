package com.to_do_list_app.service;

import com.to_do_list_app.model.ToDoList;
import com.to_do_list_app.model.User;
import com.to_do_list_app.repository.ToDoListRepo;
import com.to_do_list_app.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;

@Service
public class ToDoListService {

    @Autowired
    ToDoListRepo toDoListRepo;


    public int add(ToDoList toDoList) throws SQLException { ;
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



}
