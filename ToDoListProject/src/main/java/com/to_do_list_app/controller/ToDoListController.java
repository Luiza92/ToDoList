package com.to_do_list_app.controller;


import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.Image;
import com.to_do_list_app.model.ToDoList;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Controller
public class ToDoListController {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ApproveService approveService;

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


    @PostMapping(path = "/api/ToDoList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object addToDoList(@ModelAttribute ToDoList modelTO, @RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();

        try {
            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId = accessToken1.getUserId();

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            Timestamp timeExpires = modelTO.getExpiredAt();


            if ((currentTimestamp.after(timeExpires))) {
                res.put("error_message", "invalid timeExpires");
                return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            int isComplete = modelTO.getIsComplete();

            if (!(isComplete == 0 || isComplete == 1)) {
                res.put("error_message", "invalid isComplete");
                return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);

            }

            modelTO.setUserId(userId);
            int toDoListId = this.toDoListService.add(modelTO);

            modelTO.setId(toDoListId);

            return new ResponseEntity<>(modelTO.toJsonString(), HttpStatus.OK);

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


    @GetMapping(path = "/api/ToDoLists", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllToDoLists(@RequestHeader Map<String, String> headers) throws SQLException {
        JSONObject res = new JSONObject();

        String bearer = headers.get("authorization");
        String accessToken = bearer.replace("Bearer ", "");

        String aToken = accessToken;
        AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

        int userId1 = accessToken1.getUserId();

        res.put("toDoLists", new JSONArray());
       res.put("count", this.toDoListService. getToDoListCount(userId1));


        try {


            ToDoList toDoList2 = this.toDoListService.getByUserId(userId1);

            List<ToDoList> toDoList = this.toDoListService.getAllToDoLists(userId1);
            toDoList.forEach(toDoList1 -> {

                res.getJSONArray("toDoLists").put(toDoList1.toJson());

            });

            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping(path = "/api/ToDoList/{toDoList_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteToDoList(@PathVariable("toDoList_id") int toDoListId, @RequestHeader Map<String, String> headers) throws JSONException {
        JSONObject res = new JSONObject();
        try {

            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId = accessToken1.getUserId();

            ToDoList toDoList = this.toDoListService.getByUserId(userId);

            if (toDoList == null) {
                res.put("message", "not found ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            this.toDoListService.delete(toDoListId);

            res.put("message", "Deleted");
            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }

    }


    @PutMapping(path = "/api/ToDoList/{toDoList_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateToDoList(@ModelAttribute ToDoList modelTO, @RequestHeader Map<String, String> headers, @PathVariable("toDoList_id") int toDoListId) throws JSONException {

        JSONObject res = new JSONObject();
        try {


            String bearer = headers.get("authorization");
            String accessToken = bearer.replace("Bearer ", "");

            String aToken = accessToken;
            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId = accessToken1.getUserId();

            ToDoList toDoList = this.toDoListService.getByUserId(userId);

            toDoList.setId(toDoListId);
            toDoList.setTitle(modelTO.getTitle());
            toDoList.setDescription(modelTO.getDescription());
            toDoList.setCreatedAt(modelTO.getCreatedAt());
            toDoList.setExpiredAt(modelTO.getExpiredAt());
            toDoList.setIsComplete(modelTO.getIsComplete());
            toDoList.setUserId(userId);

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            Timestamp timeExpires = toDoList.getExpiredAt();


            if ((currentTimestamp.after(timeExpires))) {
                res.put("error_message", "invalid timeExpires");
                return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            int isComplete = toDoList.getIsComplete();

            if (!(isComplete == 0 || isComplete == 1)) {
                res.put("error_message", "invalid isComplete");
                return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);

            }


            this.toDoListService.update(toDoList);

            return new ResponseEntity<>(toDoList.toJsonString(), HttpStatus.OK);


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
