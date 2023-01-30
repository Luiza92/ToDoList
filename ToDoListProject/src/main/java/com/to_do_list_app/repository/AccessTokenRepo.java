package com.to_do_list_app.repository;

import com.to_do_list_app.model.AccessToken;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Map;
@Repository
public class AccessTokenRepo implements AccessTokenRepository {
    private final JdbcTemplate jdbcTemplate;

    public AccessTokenRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public AccessToken get(int id) throws SQLException {
        String sql = (Query.accessTokenGet);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        AccessToken accessToken = new AccessToken();

        accessToken.setId((int) result.get("id"));
        accessToken.setUserId((int) result.get("user_id"));
        accessToken.setToken((String) result.get("token"));
        accessToken.setCreatedAt((Timestamp) result.get("created_at"));
        accessToken.setExpiredAt((Timestamp) result.get("expired_at"));
        accessToken.setRefreshTokenId((int) result.get("refresh_token_id"));

        Integer accessTokenId = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;
        accessToken.setId(accessTokenId);
        return accessToken;
    }

    @Override
    public int insert(AccessToken accessToken) throws SQLException {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(Query.accessTokenInsert, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, accessToken.getUserId());
                statement.setString(2, accessToken.getToken());
                statement.setTimestamp(3, accessToken.getCreatedAt());
                statement.setTimestamp(4, accessToken.getExpiredAt());
                statement.setInt(5, accessToken.getRefreshTokenId());

                return statement;
            }
        }, holder);

        return holder.getKey().intValue();
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = Query.accessTokenDelete;
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            System.out.println("accessToken has been  delete.");
            return false;
        }
        return true;
    }

    @Override
    public int update(AccessToken accessToken) throws SQLException {
        String sql = (Query.accessTokenUpdate);
        System.err.println(accessToken.getUserId() + ", " + accessToken.getToken() + ", " + accessToken.getCreatedAt() + ", " + accessToken.getExpiredAt() + ", " + accessToken.getRefreshTokenId() + "," + accessToken.getId());
        int result = jdbcTemplate.update(sql, accessToken.getUserId(), accessToken.getToken(), accessToken.getCreatedAt(), accessToken.getExpiredAt(), accessToken.getRefreshTokenId(), accessToken.getId());
        if (result > 0) {
            System.out.println("accessToken has been update.");
            return accessToken.getId();
        }
        return accessToken.getId();
    }

    @Override
    public int deleteByUserId(int userId) throws SQLException {
        return jdbcTemplate.update(Query.accessTokenDeleteByUserId,
                userId);
    }

    @Override
    public boolean deleteByToken(String token) throws SQLException {
        String sql = Query.deleteByAccessToken;
        int result = jdbcTemplate.update(sql, token);
        if (result == 0) {
            System.out.println("accessToken has been  delete.");
            return false;
        }
        return true;
    }

    @Override
    public AccessToken getByAccessToken(String token) throws SQLException {
        return jdbcTemplate.query("select * from access_token where token = ?",
                new ResultSetExtractor<AccessToken>() {
                    @Override
                    public AccessToken extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {

                            AccessToken a = new AccessToken();

                            a.setId(rs.getInt("id"));
                            a.setUserId(rs.getInt("user_id"));
                            a.setToken(rs.getString("token"));
                            a.setCreatedAt(rs.getTimestamp("created_at"));
                            a.setExpiredAt(rs.getTimestamp("expired_at"));
                            a.setRefreshTokenId(rs.getInt("refresh_token_id"));
                          //  a.setRefreshToken(rs.getString("refreshToken"));

                            return a;
                        }

                        return null;
                    }
                },
                token);
    }
}
