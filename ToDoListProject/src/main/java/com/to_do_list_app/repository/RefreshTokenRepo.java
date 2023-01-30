package com.to_do_list_app.repository;

import com.to_do_list_app.model.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Map;

@Repository
public class RefreshTokenRepo implements RefreshTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public RefreshTokenRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public RefreshToken get(int id) throws SQLException {

        String sql = (Query.refreshTokenGet);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setId((int) result.get("Id"));
        refreshToken.setUserId((int) result.get("user_id"));
        refreshToken.setToken((String) result.get("token"));
        refreshToken.setCreatedAt((Timestamp) result.get("created_at"));
        refreshToken.setExpiredAt((Timestamp) result.get("expired_at"));



        Integer refreshTokenId = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;
        refreshToken.setId(refreshTokenId);
        return refreshToken;
    }

    @Override
    public int insert(RefreshToken refreshToken) throws SQLException {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(Query.refreshTokenInsert, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, refreshToken.getUserId());
                statement.setString(2, refreshToken.getToken());
                statement.setTimestamp(3, refreshToken.getCreatedAt());
                statement.setTimestamp(4, refreshToken.getExpiredAt());
                return statement;
            }
        }, holder);

        return holder.getKey().intValue();
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = Query.refreshTokenDelete;
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            System.out.println("refreshToken has been  delete.");
            return false;
        }
        return true;

    }


    @Override
    public int update(RefreshToken refreshToken) throws SQLException {
        String sql = Query.refreshTokenUpdate;
        System.err.println(refreshToken.getUserId() + ", " + refreshToken.getToken() + ", " + refreshToken.getCreatedAt() + ", " + refreshToken.getExpiredAt() + ", " + refreshToken.getId());
        int result = jdbcTemplate.update(sql, refreshToken.getUserId(), refreshToken.getToken(), refreshToken.getCreatedAt(), refreshToken.getExpiredAt() , refreshToken.getId());
        if (result > 0) {
            return refreshToken.getId();
        }
        return refreshToken.getId();
    }

    @Override
    public int deleteByUserId(int userId) throws SQLException {
        return jdbcTemplate.update(
                Query.refreshTokenDeleteByUserId,
                userId);

    }

    @Override
    public boolean deleteByToken(String token) throws SQLException {

            String sql = Query.deleteByRefreshToken;
            int result = jdbcTemplate.update(sql, token);
            if (result == 0) {
                System.out.println("refreshToken has been  delete.");
                return false;
            }
            return true;
        }



    @Override
    public RefreshToken getByRefreshToken(String token) throws SQLException {
        System.err.println("refreshToken  " + token);
        return jdbcTemplate.query(
                " select * from refresh_token where token = ?" ,
                new ResultSetExtractor<RefreshToken>() {
                    @Override
                    public RefreshToken extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {

                            RefreshToken rt = new RefreshToken();


                            rt.setId(rs.getInt("id"));
                            rt.setUserId(rs.getInt("user_id"));
                            rt.setToken(rs.getString("token"));
                            rt.setCreatedAt(rs.getTimestamp("created_at"));
                            rt.setExpiredAt(rs.getTimestamp("expired_at"));



                            return rt;
                        }

                        return null;
                    }
                },
                token);

    }

//      @Override
//    public RefreshToken getByRefreshToken(String token) throws SQLException {
//        System.err.println("refreshToken  " + token);
//        return jdbcTemplate.query(
//                " SELECT refresh_token.*, access_token.`id` AS accessTokenId\n" +
//                        "            FROM access_token\n" +
//                        "            LEFT JOIN refresh_token ON access_token.`refresh_token_id` = refresh_token.`id`\n" +
//                        "            WHERE refresh_token.`token` = ?" ,
//                new ResultSetExtractor<RefreshToken>() {
//                    @Override
//                    public RefreshToken extractData(ResultSet rs) throws SQLException,
//                            DataAccessException {
//                        if (rs.next()) {
//
//                            RefreshToken rt = new RefreshToken();
//
//
//                            rt.setId(rs.getInt("id"));
//                            rt.setUserId(rs.getInt("user_id"));
//                            rt.setToken(rs.getString("token"));
//                            rt.setCreatedAt(rs.getTimestamp("created_at"));
//                            rt.setExpiredAt(rs.getTimestamp("expired_at"));
//                            rt.setAccessTokenId(rs.getInt("accessTokenId"));
//
//
//                            return rt;
//                        }
//
//                        return null;
//                    }
//                },
//                token);
//
//    }
//

    @Override
    public RefreshToken getByUserId(int uid) throws SQLException {
        return jdbcTemplate.query(
                " SELECT * FROM refresh_token WHERE user_id = ? order by id desc" ,
                new ResultSetExtractor<RefreshToken>() {
                    @Override
                    public RefreshToken extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {

                            RefreshToken rt = new RefreshToken();
                            rt.setId(rs.getInt("id"));
                            rt.setUserId(rs.getInt("user_id"));
                            rt.setToken(rs.getString("token"));
                            rt.setCreatedAt(rs.getTimestamp("created_at"));
                            rt.setExpiredAt(rs.getTimestamp("expired_at"));
                            return rt;
                        }

                        return null;
                    }
                },uid);
    }






}
