package com.to_do_list_app.controller;

import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.Approve;
import com.to_do_list_app.model.Image;
import com.to_do_list_app.model.User;
import com.to_do_list_app.service.*;
import com.to_do_list_app.validation.UserValidation;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class Registration {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    @Autowired
    private ImageService imageService;

    @Autowired
    private ApproveService approveService;
    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private ToDoListService toDoListService;
    @Autowired
    private RegistrationService registrationService;

    User user = new User();

    UserValidation userValidation = new UserValidation();


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
    public ResponseEntity<?> updateProfile(@ModelAttribute User modelTO, @RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) throws JSONException {

        JSONObject res = new JSONObject();

        try {
            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");


            if (userValidation.isValidUsername(modelTO.getUsername()) == false) {
                res.put("error_message", "Error Invalid Username");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (userValidation.isValidFirstName(modelTO.getFirstName()) == false) {
                res.put("error_message", "Error Invalid FirstName");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (userValidation.isValidLastName(modelTO.getLastName()) == false) {
                res.put("error_message", "Error Invalid LastName ");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (userValidation.isValidEmail(modelTO.getEmail()) == false) {
                res.put("error_message", "Error Invalid Email");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (!modelTO.getPassword().equals(modelTO.getConfirmPassword())) {
                res.put("error message - ", "password does not match");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (userValidation.isValidPassword(modelTO.getPassword()) == false) {
                res.put("error_message", "Error Invalid Password");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            user.setPassword(new BCryptPasswordEncoder().encode(modelTO.getPassword()));


            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId = accessToken1.getUserId();

            User user = this.userService.getByUserId(userId);

            Image img = this.imageService.get(user.getImageId());
            JSONObject fileObject = img.toJson();

            if (file.isEmpty()) {
                res.put("error_message", "not found image ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            if (file != null) {

                if (file.getContentType().startsWith("image") == false) {
                    res.put("error_message", "invalid image file");
                    return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
                }
                this.imageService.deleteFile(img);
                this.imageService.delete(img.getId());
                fileObject = this.imageService.saveFile(file, "image");
                System.err.println(fileObject + "- fileObject");
            }

            user.setImage(fileObject);
            user.setImageId(fileObject.getInt("id"));

            JSONObject roleObject = this.roleService.roleGet(user.getRoleId());

            System.out.println("rolId " + roleObject);

            user.setUsername(modelTO.getUsername());
            user.setFirstName(modelTO.getFirstName());
            user.setLastName(modelTO.getLastName());
            user.setEmail(modelTO.getEmail());
            user.setUpdatedAt(modelTO.getUpdatedAt());


            this.userService.update(user);

            return new ResponseEntity<>(user.toJsonString(), HttpStatus.OK);

        } catch (NullPointerException ex) {
            res.put("error_message", "NullPointerException");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        } catch (DuplicateKeyException ex) {
            res.put("error_message", "DUPLICATE ERROR MESSAGE ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception ex) {
            res.put("error_message", "this user not found ");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping(path = "/api/getProfile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProfile(@RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();
        try {
            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId = accessToken1.getUserId();

            User user = this.userService.getByUserId(userId);

            int imageId = user.getImageId();
            Image image = this.imageService.get(imageId);

            user.setImage(image.toJson());

            return new ResponseEntity<>(user.toJsonString(), HttpStatus.OK);

        } catch (Exception ex) {
            res.put("error_message", "this user not found ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/api/deleteProfile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteProfile(@RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();
        try {

            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");


            if (accessToken == null) {
                res.put("message", "not found ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            String aToken = accessToken;

            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId = accessToken1.getUserId();

            User user = this.userService.getByUserId(userId);

            if (user == null) {
                res.put("message", "not found ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            int imageId = user.getImageId();
            Image image = this.imageService.get(imageId);

            if (image != null) {
                this.imageService.delete(imageId);
                this.imageService.deleteFile(image);
            }

            this.accessTokenService.deleteByUserId(userId);
            this.refreshTokenService.deleteByUserId(userId);
            this.toDoListService.deleteBYUserId(userId);
            this.approveService.deleteByUserId(userId);
            this.userService.delete(userId);

            res.put("message", "Deleted");
            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }

    }

}









