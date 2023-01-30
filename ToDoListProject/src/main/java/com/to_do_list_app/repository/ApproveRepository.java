package com.to_do_list_app.repository;

import com.to_do_list_app.model.Approve;

import java.sql.SQLException;

public interface ApproveRepository {


    Approve get(int id) throws SQLException;

    int insert(Approve approve) throws SQLException;

    Approve delete(int id) throws SQLException;

    int update(Approve approve) throws SQLException;

    int deleteByUserId(int userId)throws SQLException;
    Approve getByRandomId(String randomId)throws SQLException;


}
