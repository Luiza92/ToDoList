package com.to_do_list_app.controller;

import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.Image;
import com.to_do_list_app.model.User;
import com.to_do_list_app.service.*;
import com.to_do_list_app.validation.UserValidation;
import org.json.JSONArray;
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
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ApproveService approveService;

    @Autowired
    private RoleService roleService;


    @Autowired
    private ToDoListService toDoListService;

    @Autowired
    private ImageService imageService;

    User user = new User();

    UserValidation userValidation = new UserValidation();

    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private RefreshTokenService refreshTokenService;
 @Autowired
    private RegistrationService registrationService;

    @PostMapping(path = "/api/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object addUser(@ModelAttribute User modelTO, @RequestParam("file") MultipartFile file,  @RequestHeader Map<String, String> headers ) throws JSONException {
        JSONObject res = new JSONObject();

        try {
//
//            String bearer = headers.get("authorization");
//            String accessToken = bearer.replace("Bearer ", "");
//
//
//            String aToken = accessToken;
//            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);
//
//            int userId1 = accessToken1.getUserId();
//
//            User user1 = this.userService.getByUserId(userId1);
//
//            if (user1.getRoleId() != 1){
//                res.put("error_message", "access denied ");
//                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
//            }

            return  userService.userAdd(modelTO, file);

        } catch (DuplicateKeyException ex) {
            res.put("error_message", "DUPLICATE ERROR MESSAGE ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            res.put("error_message", "null err ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not exist ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping(path = "/api/user/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable("user_id") int userId , @RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();
        try {

            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);


            int userId1 = accessToken1.getUserId();

            User user1 = this.userService.getByUserId(userId1);

            if (user1.getRoleId() != 1){
                res.put("error_message", "access denied ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            User user = this.userService.get(userId);

            int imageId = user.getImageId();
            Image image = this.imageService.get(imageId);

            int roleId = user.getRoleId();
            user.setRole(this.roleService.roleGet(roleId));

            user.setImage(image.toJson());

            return new ResponseEntity<>(user.toJsonString(), HttpStatus.OK);

        } catch (Exception ex) {
            res.put("error_message", "this user not found ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(@RequestParam(name = "limit") String limit,
                                         @RequestParam(name = "skip") String skip , @RequestHeader Map<String, String> headers) {
        JSONObject res = new JSONObject();

        if ((Integer.parseInt(limit) < 0) || (Integer.parseInt(skip) < 0)) {
            res.put("message", "Value cannot be negative");
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }

        res.put("users", new JSONArray());
        res.put("count", this.userService. getUserCount());


        try {
             String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");


            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId1 = accessToken1.getUserId();

            User user2 = this.userService.getByUserId(userId1);

            if (user2.getRoleId() != 1){
                res.put("error_message", "access denied ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }



            List<User> user = this.userService.getAllUsers(limit, skip);
            user.forEach(user1 -> {

                int imageId = user1.getImageId();
                Image image = null;
                try {
                    image = this.imageService.get(imageId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (image != null)
                    user1.setImage(image.toJson());


                int roleId = user1.getRoleId();
                try {
                    user1.setRole(this.roleService.roleGet(roleId));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                res.getJSONArray("users").put(user1.toJson());

            });

            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping(path = "/api/user/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable("user_id") int userId , @RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();

        try {

            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");


            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId1 = accessToken1.getUserId();

            User user2 = this.userService.getByUserId(userId1);

            int roleId = user2.getRoleId();

            if (roleId != 1){
                res.put("error_message", "access denied");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            if (this.userService.getUserAdminCount(roleId) == true){
                res.put("error_message", "44444444444");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }


            User user = this.userService.get(userId);

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

    @PutMapping(path = "/api/user/{user_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@ModelAttribute User modelTO, @RequestParam("file") MultipartFile file, @PathVariable("user_id") int userId,  @RequestHeader Map<String, String> headers) throws JSONException {

        JSONObject res = new JSONObject();
        try {

            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");


            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId1 = accessToken1.getUserId();

            User user2 = this.userService.getByUserId(userId1);

            if (user2.getRoleId() != 1 ){
                res.put("error_message", "access denied");
                return new ResponseEntity<>(res.toString(), HttpStatus.FORBIDDEN);
            }

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

            User user = this.userService.get(userId);

            user.setPassword(new BCryptPasswordEncoder().encode(modelTO.getPassword()));

            System.out.println("image    " + user.getImageId());

            Image img = this.imageService.get(user.getImageId());

            JSONObject fileObject = img.toJson();

            if (file.isEmpty()) {
                res.put("error_message", "not found image ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            if (file.getContentType().startsWith("image") == false) {
                res.put("error_message", "invalid image file");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
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


}
