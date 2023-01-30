package com.to_do_list_app.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.sql.Timestamp;


@Getter
@Setter
public class AccessToken {


    private int id;
    private int userId;
    private String token;
    private Timestamp createdAt;
    private Timestamp expiredAt;
    private int refreshTokenId;
    private String refreshToken;

    public AccessToken() {
    }

    public JSONObject toJson() {

        JSONObject res = new JSONObject();

        res.put("userId", getUserId());
        res.put("accessToken", getToken());
        res.put("createdAt", getCreatedAt());
        res.put("expiredAt", getExpiredAt());
        res.put("refreshToken", getRefreshToken());

        return res;
    }

    public String toJsonString() {
        return toJson().toString();
    }


}
