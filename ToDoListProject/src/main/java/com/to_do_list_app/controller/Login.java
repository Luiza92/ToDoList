package com.to_do_list_app.controller;

import com.to_do_list_app.model.User;
import com.to_do_list_app.service.AccessTokenService;
import com.to_do_list_app.service.AuthService;
import com.to_do_list_app.service.RefreshTokenService;
import com.to_do_list_app.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Login {



    @Autowired
    private AuthService authService;


    @PostMapping(path = "/api/login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@ModelAttribute User modelTO) throws JSONException {
        JSONObject res = new JSONObject();
        try {
            return  authService.login(modelTO);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found " + ex.getMessage());
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }

}
