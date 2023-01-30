package com.to_do_list_app.controller;

import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.RefreshToken;
import com.to_do_list_app.model.User;
import com.to_do_list_app.service.AccessTokenService;
import com.to_do_list_app.service.AuthService;
import com.to_do_list_app.service.RefreshTokenService;
import com.to_do_list_app.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

@Controller
public class RefreshTokenController {


    @Autowired
    private AuthService authService;


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    User user = new User();


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Test {
        private String refreshToken;

    }


    @PostMapping(path = "/api/RefreshToken", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> RefreshToken(@ModelAttribute Test data, @RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();

        try {
            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            return authService.refresh(data.refreshToken, accessToken);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found " + ex.getMessage());
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }


}