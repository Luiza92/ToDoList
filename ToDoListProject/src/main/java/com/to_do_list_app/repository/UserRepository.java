package com.to_do_list_app.repository;

import com.to_do_list_app.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserRepository {



    int insert(User user) throws SQLException;

    User get(int id) throws SQLException;

    User getByUsername(String username) throws SQLException;

    User getByUsernameAndId(String username, int id) throws SQLException;

    User getByEmail(String email) throws SQLException;

    User getByEmailAndId (String email, int id) throws SQLException;

    User getByUserId(int userId) throws SQLException;



    boolean delete(int id) throws SQLException;

    int update(User user) throws SQLException;

    List<User> getAllUsers(String limit, String skip);

}
