package com.to_do_list_app.controller;

import com.to_do_list_app.model.Approve;
import com.to_do_list_app.model.Image;
import com.to_do_list_app.model.User;
import com.to_do_list_app.service.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
import java.sql.Timestamp;

@Controller
public class Registration {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ApproveService approveService;

    @Autowired
    private RegistrationService registrationService;


    @PostMapping(path = "/api/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registration(@ModelAttribute User modelTO, @RequestParam("file") MultipartFile file) throws JSONException {
        JSONObject res = new JSONObject();
        try {
            return registrationService.registration(modelTO, file);

        } catch (Exception ex) {
            res.put("error_message", "server error  ");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/api/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addApprove(@RequestParam("user_id") int userId,
                                        @RequestParam("random_id") String randomId) throws Exception {
        JSONObject res = new JSONObject();

        Approve approve = this.approveService.getByRandomId(randomId);

        if (approve == null) {
            res.put("error_message", "approve ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());

        Timestamp timeExpires = approve.getTimeExpires();


        if ((currentTimestamp.before(timeExpires)) & randomId != null) {

            int approveDelete = this.approveService.deleteByUserId(userId);
            User user1 = this.userService.getByUserId(userId);
            user1.setStatus(1);
            this.userService.update(user1);

            System.out.println("status1  " + user1.getStatus());
            return new ResponseEntity<>("You have successfully registration ", HttpStatus.OK);

        }
        User user1 = this.userService.getByUserId(userId);

        int imageId = user1.getImageId();
        Image image = this.imageService.get(imageId);

        if (image != null) {
            this.imageService.delete(imageId);
            this.imageService.deleteFile(image);
        }
        int approveDelete = this.approveService.deleteByUserId(userId);
        this.userService.delete(userId);

        res.put("error_message", "time elapsed ");
        return new ResponseEntity<>("time elapsed ", HttpStatus.NOT_FOUND);


    }

    @PutMapping(path = "/api/editProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProfile(@ModelAttribute User modelTO, @RequestParam(required = false, name="file") MultipartFile file, @RequestHeader Map<String, String> headers) throws JSONException {

        JSONObject res = new JSONObject();
        try {
            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            return registrationService.editProfile(modelTO, file, accessToken);

        } catch (Exception ex) {
            res.put("error_message", "server error  ");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(path = "/api/getProfile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProfile(@RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();
        try {
            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            return registrationService.getProfile(accessToken);

        } catch (Exception ex) {
            res.put("error_message", "server error  ");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping(path = "/api/deleteProfile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProfile(@RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();
        try {

            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            return registrationService.deleteProfile(accessToken);


        } catch (Exception ex) {
            res.put("error_message", "server error  ");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }
    }


}









