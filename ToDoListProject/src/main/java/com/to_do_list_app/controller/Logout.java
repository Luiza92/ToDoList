package com.to_do_list_app.controller;

import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.RefreshToken;
import com.to_do_list_app.service.AccessTokenService;
import com.to_do_list_app.service.RefreshTokenService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class Logout {


    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping(path = "/api/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object logout(HttpServletRequest req, @RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();
        try {

            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int refreshTokenId = accessToken1.getRefreshTokenId();
            RefreshToken refreshToken = this.refreshTokenService.get(refreshTokenId);

            this.accessTokenService.deleteByAccessToken(accessToken1.getToken());
            this.refreshTokenService.deleteByRefreshToken(refreshToken.getToken());

            res.put("message", "this user logout ");
            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (Exception ex) {
            res.put("error_message", " not exist ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNAUTHORIZED);
        }
    }

}
