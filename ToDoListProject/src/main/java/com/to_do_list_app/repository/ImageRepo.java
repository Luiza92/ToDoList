package com.to_do_list_app.repository;

import com.to_do_list_app.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class ImageRepo implements ImageRepository{



    private final JdbcTemplate jdbcTemplate;

    public ImageRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Image get(int id) throws SQLException {
        String sql = (Query.imageGet);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, id);
        if (rows.size() == 0)
            return null;
        Map<String, Object> result = rows.get(0);

        Image image = new Image();

        image.setFileName((String) result.get("file_name"));
        image.setPath((String) result.get("path"));


        Integer imageId = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;
        image.setId(imageId);
        return image;
    }

    @Override
    public int insert(Image image) throws SQLException {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(Query.imageAdd, Statement.RETURN_GENERATED_KEYS);

                statement.setString(1, image.getFileName());
                statement.setString(2, image.getPath());


                return statement;
            }
        }, holder);

        return ((Integer) holder.getKey().intValue()).intValue();
    }


    @Override
    public boolean delete(int id) throws SQLException {
        String sql = (Query.imageDelete);
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            return false;
        }
        return true;
    }

    @Override
    public int update(Image image) throws SQLException {
        String sql = (Query.imageUpdate);
        System.err.println(image.getFileName() + ", " + image.getPath()  + ", " +  image.getId());
        int result = jdbcTemplate.update(sql, image.getFileName() , image.getPath(),  image.getId());

        if (result > 0) {
            return image.getId();
        }
        return -1;
    }
}
