package com.to_do_list_app.service;

import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.Approve;
import com.to_do_list_app.model.Image;
import com.to_do_list_app.model.User;
import com.to_do_list_app.repository.UserRepo;
import com.to_do_list_app.validation.UserValidation;
import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {


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
    private RoleService roleService;

    UserValidation userValidation = new UserValidation();

    @Autowired
    UserRepo userRepo;
    private final JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int add(User user) throws SQLException {
        ;
        int userId = this.userRepo.insert(user);
        return userId;
    }

    public User get(int id) throws SQLException {
        return this.userRepo.get(id);
    }

    public User getByUsername(String username) throws SQLException {
        return this.userRepo.getByUsername(username);
    }

    public User getByUsernameAndId(String username, int id) throws SQLException {
        return this.userRepo.getByUsernameAndId(username, id);
    }

    public User getByEmail(String email) throws SQLException {
        return this.userRepo.getByEmail(email);
    }

    public User getByUserId(int userId) throws SQLException {
        return this.userRepo.getByUserId(userId);
    }

    public List<User> getAllUsers(String limit, String skip) {
        return this.userRepo.getAllUsers(limit, skip);

    }

    public boolean delete(int id) throws SQLException {
        return this.userRepo.delete(id);

    }

    public int update(User user) throws SQLException {
        int userId = this.userRepo.update(user);
        return userId;
    }

    public boolean getUserAdminCount(int roleId) throws SQLException {

        String sql = ("SELECT COUNT(*) AS _count FROM users WHERE role_id = ?");
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, roleId);
        long count = (long) result.get("_count");

        return (count == 1);
    }

    public int getUserCount() {
        String sql = ("select count(*) AS _count from users");
        Map<String, Object> result = jdbcTemplate.queryForMap(sql);
        long count = (long) result.get("_count");

        return (int) count;
    }


    @Autowired
    private JavaMailSender sender;

    private void sendEmail(String mail, String link, Timestamp date) throws Exception {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String soapXML = getResourceFileAsString("html/Conform.html");

        helper.setTo(mail);
        helper.setText(soapXML.replace("{link}", link).replace("{date}", date.toString()), true);

        helper.setSubject("Hello");
        sender.send(message);
    }

    public class GenerateUUID {

        public String Generate() {
            UUID uuid = UUID.randomUUID();
            return uuid.toString();
        }
    }

    public static String getResourceFileAsString(String fileName) {
        InputStream is = getResourceFileAsInputStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return (String) reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            throw new RuntimeException("resource not found");
        }
    }

    public static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = RegistrationService.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    public ResponseEntity<String> allUsers(String limit, String skip) throws SQLException {
        JSONObject res = new JSONObject();

        if ((Integer.parseInt(limit) < 0) || (Integer.parseInt(skip) < 0)) {
            res.put("message", "Value cannot be negative");
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }

        res.put("users", new JSONArray());
        res.put("count", this.getUserCount());

        try {

            List<User> user = this.getAllUsers(limit, skip);
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

    public ResponseEntity<String> userAdd(@NonNull User modelTO, @NonNull MultipartFile file) throws SQLException {
        JSONObject res = new JSONObject();
        try {

            String username = modelTO.getUsername();
            String email = modelTO.getEmail();
            int roleId = modelTO.getRoleId();


            if (modelTO.getUsername() == null || !userValidation.isValidUsername(modelTO.getUsername())) {
                res.put("error_message", "Error Invalid Username ");
                System.out.println("username- " + res);
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }
            if (this.getByUsername(username) != null) {
                res.put("error message ", "DUPLICATE USERNAME");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getFirstName() == null || !userValidation.isValidFirstName(modelTO.getFirstName())) {
                res.put("error_message", "Error Invalid FirstName ");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getLastName() == null || !userValidation.isValidLastName(modelTO.getLastName())) {
                res.put("error_message", "Error Invalid LastName ");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getEmail() == null || !userValidation.isValidEmail(modelTO.getEmail())) {
                res.put("error_message", "Error Invalid Email");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (this.getByEmail(email) != null) {
                res.put("error message ", "DUPLICATE EMAIL");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getConfirmPassword() == null || modelTO.getPassword() == null || !modelTO.getPassword().equals(modelTO.getConfirmPassword())) {
                res.put("error message - ", "password does not match");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (!userValidation.isValidPassword(modelTO.getPassword())) {
                res.put("error_message", "Error Invalid Password");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (file.isEmpty()) {
                res.put("error_message", "not found image ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
                res.put("error_message", "invalid image file");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }
            if (roleId >= 4 || roleId <= 0) {
                res.put("error_message", "invalid roleId  ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }


            GenerateUUID generateUUID = new GenerateUUID();
            String randomId = generateUUID.Generate();
            System.err.println(randomId);


            Timestamp expiresTimestamp = new Timestamp(System.currentTimeMillis());


            Calendar c = Calendar.getInstance();
            c.setTime(expiresTimestamp);
            c.add(Calendar.MINUTE, (1));
            Timestamp TimeExpiresDateExDate = new Timestamp(c.getTimeInMillis());

            JSONObject fileObject = this.imageService.saveFile(file, "image");

            modelTO.setImageId(fileObject.getInt("id"));
            modelTO.setImage(fileObject);


            User user = new User(modelTO, TimeExpiresDateExDate, fileObject);

            user.setUsername(modelTO.getUsername());
            user.setFirstName(modelTO.getFirstName());
            user.setLastName(modelTO.getLastName());
            user.setEmail(modelTO.getEmail());
            user.setPassword(new BCryptPasswordEncoder().encode(modelTO.getPassword()));
            user.setImage(fileObject);
            user.setStatus(0);
            user.setRoleId(modelTO.getRoleId());


            int userId = this.add(user);

            Approve approve = new Approve();

            approve.setUserId(userId);
            approve.setRandomId(randomId);
            approve.setTimeExpires(TimeExpiresDateExDate);


            int approve1 = this.approveService.insert(approve);

            User user1 = this.get(userId);

            String link = (String.format("http://127.0.0.1:8088/api/approve?user_id=%s&random_id=%s", userId, randomId));

            System.out.println("userId " + userId);
            System.out.println("randomId " + randomId);
            System.err.println("link : " + link);

            this.sendEmail(user.getEmail(), link, TimeExpiresDateExDate);


            res.put("id", user1.getId());
            res.put("username", user1.getUsername());
            res.put("firstName", user1.getFirstName());
            res.put("lastName", user1.getLastName());
            res.put("email", user1.getEmail());
            res.put("imageId", fileObject);
            res.put("TimeExpires", approve.getTimeExpires());


            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (DuplicateKeyException ex) {
            res.put("error_message", "DUPLICATE ERROR MESSAGE ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception ex) {
            res.put("error_message", "server error  ");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity<String> userDelete(int userId, String accessToken) throws SQLException {
        JSONObject res = new JSONObject();

        try {
            String aToken = accessToken;

            AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);

            int userId1 = accessToken1.getUserId();

            User user2 = this.getByUserId(userId1);

            int roleId = user2.getRoleId();

            if (roleId != 1) {
                res.put("error_message", "access denied");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            User user = this.get(userId);

            if (user == null) {
                res.put("message", "not found ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            int roleId1 = user.getRoleId();

            if (roleId1 == 1) {
                if (this.getUserAdminCount(roleId) == true) {
                    res.put("error_message", "can't delete last admin");
                    return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
                }
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
            this.delete(userId);

            res.put("message", "Deleted");
            return new ResponseEntity<>(res.toString(), HttpStatus.OK);

        } catch (
                Exception ex) {
            ex.printStackTrace();
            res.put("error_message", "not found");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<String> userPut(User modelTO, MultipartFile file, int userId) {


        JSONObject res = new JSONObject();
        try {

            User user = this.get(userId);

            if (user == null) {
                res.put("error_message", "user not found ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            if (modelTO.getUsername() != null && userValidation.isValidUsername(modelTO.getUsername()) == false) {
                res.put("error_message", "Error Invalid Username");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getFirstName() != null && userValidation.isValidFirstName(modelTO.getFirstName()) == false) {
                res.put("error_message", "Error Invalid FirstName");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getLastName() != null && userValidation.isValidLastName(modelTO.getLastName()) == false) {
                res.put("error_message", "Error Invalid LastName ");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getEmail() != null && userValidation.isValidEmail(modelTO.getEmail()) == false) {
                res.put("error_message", "Error Invalid Email");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (modelTO.getConfirmPassword() != null && modelTO.getPassword() != null) {

                if (!modelTO.getPassword().equals(modelTO.getConfirmPassword())) {
                    res.put("error message - ", "password does not match");
                    return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
                }

                if (modelTO.getPassword() != null && userValidation.isValidPassword(modelTO.getPassword()) == false) {
                    res.put("error_message", "Error Invalid Password");
                    return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
                }

                user.setPassword(new BCryptPasswordEncoder().encode(modelTO.getPassword()));
            }

            Image img = this.imageService.get(user.getImageId());

            JSONObject fileObject = null;


            if (file != null && !file.isEmpty()) {

                if (file.getContentType().startsWith("image") == false) {
                    res.put("error_message", "invalid image file");
                    return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
                }

                if (file.getContentType().startsWith("image") == false) {
                    res.put("error_message", "invalid image file");
                    return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
                }

                if (img != null) {
                    this.imageService.deleteFile(img);
                    this.imageService.delete(img.getId());

                }
                fileObject = this.imageService.saveFile(file, "image");
                System.err.println(fileObject + "- fileObject");

            } else {
                if (img != null) {
                    fileObject = img.toJson();
                }
            }
            if (fileObject != null) {
                user.setImage(fileObject);
                user.setImageId(fileObject.getInt("id"));
            }

            JSONObject roleObject = this.roleService.roleGet(user.getRoleId());

            if (modelTO.getUsername() != null) {
                user.setUsername(modelTO.getUsername());
            }
            if (modelTO.getFirstName() != null) {
                user.setFirstName(modelTO.getFirstName());
            }
            if (modelTO.getLastName() != null) {
                user.setLastName(modelTO.getLastName());
            }
            if (modelTO.getEmail() != null) {
                user.setEmail(modelTO.getEmail());
            }

            user.setUpdatedAt(modelTO.getUpdatedAt());
            user.setRole(roleObject);

            this.update(user);


            return new ResponseEntity<>(user.toJsonString(), HttpStatus.OK);

        } catch (
                NullPointerException ex) {
            res.put("error_message", "NullPointerException");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        } catch (
                DuplicateKeyException ex) {
            res.put("error_message", "DUPLICATE ERROR MESSAGE ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (
                Exception ex) {
            res.put("error_message", "this user not found ");
            ex.printStackTrace();
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
    }


}
