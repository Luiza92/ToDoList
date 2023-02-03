package com.to_do_list_app.controller;

import com.to_do_list_app.model.Image;
import com.to_do_list_app.model.User;
import com.to_do_list_app.service.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ImageService imageService;


    @PostMapping(path = "/api/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object addUser(@ModelAttribute User modelTO, @RequestParam("file") MultipartFile file) throws JSONException {
        JSONObject res = new JSONObject();

        try {
            if(modelTO == null){
                res.put("error_message", " you didn't provide required fields");
                return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
            return userService.userAdd(modelTO, file);

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
    public ResponseEntity<?> getUser(@PathVariable("user_id") int userId) throws JSONException {
        JSONObject res = new JSONObject();
        try {

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
                                         @RequestParam(name = "skip") String skip) {
        JSONObject res = new JSONObject();
        try {
            return userService.allUsers(limit, skip);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/api/user/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable("user_id") int userId, @RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();

        try {
            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            return userService.userDelete(userId, accessToken);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }

    }

    @PutMapping(path = "/api/user/{user_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@ModelAttribute User modelTO, @RequestParam(required = false, name="file") MultipartFile file, @PathVariable("user_id") int userId) throws JSONException {

        JSONObject res = new JSONObject();
        try {
            return userService.userPut(modelTO, file,userId);

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
