package com.to_do_list_app.repository;

import com.to_do_list_app.model.AccessToken;

import java.sql.SQLException;

public interface AccessTokenRepository {



    AccessToken get(int id) throws SQLException;

    int insert(AccessToken accessToken) throws SQLException;

    //  int delete(int id) throws SQLException;

    boolean delete(int id) throws SQLException;

    int update(AccessToken accessToken) throws SQLException;

    int deleteByUserId(int userId)throws SQLException;
    boolean deleteByToken(String token)throws SQLException;

    AccessToken getByAccessToken(String token) throws SQLException;
}
