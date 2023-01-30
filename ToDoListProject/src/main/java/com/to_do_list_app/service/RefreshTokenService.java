package com.to_do_list_app.service;

import com.to_do_list_app.model.RefreshToken;
import com.to_do_list_app.repository.RefreshTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

@Service
public class RefreshTokenService {




    @Autowired
    RefreshTokenRepo refreshTokenRepo;

    @Autowired
    AccessTokenService accessTokenService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int insert(RefreshToken refreshToken) throws SQLException {

        int refreshTokenId = this.refreshTokenRepo.insert(refreshToken);{
        }
        return refreshTokenId;
    }

    public RefreshToken get(int id) throws SQLException {

        String sql = ("select * from refresh_token where id = ? ");
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUserId((int) result.get("user_id"));
        refreshToken.setToken((String) result.get("token"));
        refreshToken.setCreatedAt((Timestamp) result.get("created_at"));
        refreshToken.setExpiredAt((Timestamp) result.get("expired_at"));


        refreshToken.setId((int) result.get("id"));
        return refreshToken;
    }

    public RefreshToken getByRefreshToken(String token) throws SQLException {
        return this.refreshTokenRepo.getByRefreshToken(token);
    }

    public RefreshToken getByUserId(int uid) throws SQLException {
        return this.refreshTokenRepo.getByUserId(uid);
    }

    public int update(RefreshToken refreshToken) throws SQLException {
        int refreshTokenId = this.refreshTokenRepo.update(refreshToken);{
        }
        return refreshTokenId;
    }


    public boolean delete(int id) throws SQLException {
        return this.refreshTokenRepo.delete(id);

    }


    public int deleteByUserId(int userId) throws SQLException {
        return this.refreshTokenRepo.deleteByUserId(userId);
    }

    public boolean deleteByRefreshToken(String token) throws SQLException {
        return this.refreshTokenRepo.deleteByToken(token);

    }


}
