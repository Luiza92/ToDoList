package com.to_do_list_app.repository;

import com.to_do_list_app.model.Image;

import java.sql.SQLException;

public interface ImageRepository {

    Image get(int id) throws SQLException;

    int insert(Image image) throws SQLException;

    boolean delete(int id) throws SQLException;

    int update(Image image) throws SQLException;

}
