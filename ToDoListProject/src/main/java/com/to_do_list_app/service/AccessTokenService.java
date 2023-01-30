package com.to_do_list_app.service;

import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.repository.AccessTokenRepo;
import com.to_do_list_app.repository.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class AccessTokenService {

    @Autowired
    AccessTokenRepo accessTokenRepo;

    @Autowired
    JdbcTemplate jdbcTemplate;


    public int insert(AccessToken accessToken) throws SQLException {

        int accessTokenId = this.accessTokenRepo.insert(accessToken);
        {
            System.err.println("add " + accessTokenId);
        }
        return accessTokenId;
    }


    public AccessToken get(int id) throws SQLException {

        String sql = (Query.accessTokenGet);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, id);

        if (result.size() == 0)
            return null;

        AccessToken accessToken = new AccessToken();


        accessToken.setId((int) result.get(0).get("id"));
        accessToken.setUserId((int) result.get(0).get("user_id"));
        accessToken.setToken((String) result.get(0).get("token"));
        accessToken.setCreatedAt((Timestamp) result.get(0).get("created_at"));
        accessToken.setExpiredAt((Timestamp) result.get(0).get("expired_at"));
        accessToken.setRefreshTokenId((int) result.get(0).get("refresh_token_id"));
        accessToken.setRefreshToken((String) result.get(0).get("refreshToken"));


        accessToken.setId((int) result.get(0).get("id"));


        return accessToken;
    }

    public AccessToken getByAccessToken(String token) throws SQLException {
        return this.accessTokenRepo.getByAccessToken(token);
    }

    public int update(AccessToken accessToken) throws SQLException {
        int accessTokenId = this.accessTokenRepo.update(accessToken);
        {
            System.err.println("add " + accessTokenId);
        }
        return accessTokenId;
    }

    public int deleteByUserId(int userId) throws SQLException {
        return this.accessTokenRepo.deleteByUserId(userId);

    }



    public boolean delete(int id) throws SQLException {
        return this.accessTokenRepo.delete(id);

    }

    public boolean deleteByAccessToken(String token) throws SQLException {
        return this.accessTokenRepo.deleteByToken(token);

    }


}