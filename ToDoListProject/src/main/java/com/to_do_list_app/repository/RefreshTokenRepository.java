package com.to_do_list_app.repository;

import com.to_do_list_app.model.RefreshToken;

import java.sql.SQLException;

public interface RefreshTokenRepository {


    RefreshToken get(int id) throws SQLException;

    int insert(RefreshToken refreshToken) throws SQLException;


    boolean delete(int id) throws SQLException;

    int update(RefreshToken refreshToken) throws SQLException;

    int deleteByUserId(int userId)throws SQLException;

    boolean deleteByToken(String token)throws SQLException;

    RefreshToken getByRefreshToken(String token) throws SQLException;

    RefreshToken getByUserId(int uid) throws SQLException;
}
