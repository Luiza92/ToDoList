package com.to_do_list_app.service;


import com.to_do_list_app.controller.Registration;
import com.to_do_list_app.model.Approve;
import com.to_do_list_app.model.Image;
import com.to_do_list_app.model.User;
import com.to_do_list_app.validation.UserValidation;
import lombok.NonNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.UUID;

import java.util.stream.Collectors;

@Service
public class RegistrationService {



    @Autowired
    private UserService userService;


    @Autowired
    private ImageService imageService;

    @Autowired
    private ApproveService approveService;

    User user = new User();

    UserValidation userValidation = new UserValidation();


    public ResponseEntity<String> registration(@NonNull User modelTO, @NonNull MultipartFile file) throws SQLException {
        JSONObject res = new JSONObject();
        try {

            String username = modelTO.getUsername();
            String email = modelTO.getEmail();


            if (userValidation.isValidUsername(modelTO.getUsername()) == false) {
                res.put("error_message", "Error Invalid Username ");
                System.out.println("username- " + res);
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }
            if (this.userService.getByUsername(username) != null) {
                res.put("error message ", "DUPLICATE USERNAME");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            if (userValidation.isValidFirstName(modelTO.getFirstName()) == false) {
                res.put("error_message", "Error Invalid FirstName ");
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

            if (this.userService.getByEmail(email) != null) {
                res.put("error message ", "DUPLICATE EMAIL");
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

            if (file.isEmpty()) {
                res.put("error_message", "not found image ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            if (file.getContentType().startsWith("image") == false) {
                res.put("error_message", "invalid image file");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
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
            user.setRoleId(3);


            int userId = this.userService.add(user);

            Approve approve = new Approve();

            approve.setUserId(userId);
            approve.setRandomId(randomId);
            approve.setTimeExpires(TimeExpiresDateExDate);


            int approve1 = this.approveService.insert(approve);

            User user1 = this.userService.get(userId);

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










}