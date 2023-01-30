package com.to_do_list_app.service;

import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.RefreshToken;
import com.to_do_list_app.model.User;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Request;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service

public class AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private RefreshTokenService refreshTokenService;


    public ResponseEntity<String> login(@NonNull User modelTO) throws SQLException, ParseException {

        JSONObject res = new JSONObject();
        String password = modelTO.getPassword();

        final User user = userService.getByUsername(modelTO.getUsername());

        if (user == null) {
            res.put("error_message", "Not found user ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }
        int userStatus = user.getStatus();

        if (userStatus != 1) {
            res.put("error_message", "user not verification");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }

        String hashedPassword = user.getPassword();

        if (BCrypt.checkpw(password, hashedPassword)) {
            final JwtProvider.TokenResult accessToken = jwtProvider.generateAccessToken(user);
            final JwtProvider.TokenResult refreshToken = jwtProvider.generateRefreshToken(user);

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());

            RefreshToken rt = new RefreshToken();

            rt.setUserId(user.getId());
            rt.setToken(refreshToken.token);
            rt.setCreatedAt(currentTimestamp);
            rt.setExpiredAt(new Timestamp(refreshToken.expiration.getTime()));


            int rid = this.refreshTokenService.insert(rt);

            if (rid == 0) {
                res.put("error_message", "Not found ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            AccessToken at = new AccessToken();

            at.setUserId(user.getId());
            at.setToken(accessToken.token);
            at.setCreatedAt(currentTimestamp);
            at.setExpiredAt(new Timestamp(accessToken.expiration.getTime()));
            at.setRefreshTokenId(rid);

            int aid = this.accessTokenService.insert(at);

            if (aid == 0) {
                res.put("error_message", "Not found ");
                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
            }

            res.put("accessToken ", accessToken.getToken());
            res.put("accessToken expiration ", accessToken.getExpiration());
            res.put("refreshToken", refreshToken.getToken());
            res.put("refreshToken expiration", refreshToken.getExpiration());

            return new ResponseEntity<>(res.toString(), HttpStatus.OK);
        } else {
            res.put("error_message", "Invalid Password ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

//    public  ResponseEntity<String> getAccessToken(@NonNull String refreshToken) throws SQLException {
//        JSONObject res = new JSONObject();
//
//        if (jwtProvider.validateRefreshToken(refreshToken)) {
//
//            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
//            final String username = claims.getSubject();
//
//            final User user = userService.getByUsername(username);
//            if (user == null) {
//                res.put("error_message", "Not found user ");
//                return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
//            }
//
//            final RefreshToken saveRefreshToken = refreshTokenService.getByUserId(user.getId());
//            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
//
//                final JwtProvider.TokenResult accessToken = jwtProvider.generateAccessToken(user);
//                res.put("accessToken ", accessToken);
//                res.put("refreshToken", refreshToken);
//                return new ResponseEntity<>(res.toString(), HttpStatus.OK);
//            }
//        }
//        res.put("error_message", "Invalid Password ");
//        return new ResponseEntity<>(res.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
//    }

    public ResponseEntity<String> refresh(@NonNull String requestRefreshToken, @NonNull String requestAccessToken) throws SQLException {
        JSONObject res = new JSONObject();

        if (jwtProvider.validateRefreshToken(requestRefreshToken) == false) {
            res.put("error_message", "Invalid  RefreshToken ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNAUTHORIZED);
        }

        if (jwtProvider.validateAccessToken(requestAccessToken) == false) {
            res.put("error_message", "Invalid  accessToken ");
            return new ResponseEntity<>(res.toString(), HttpStatus.UNAUTHORIZED);
        }

        String rfToken = requestRefreshToken;
        RefreshToken refreshToken1 = this.refreshTokenService.getByRefreshToken(rfToken);
        int userIdRefreshToken = refreshToken1.getUserId();


        String aToken = requestAccessToken;
        AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);
        int userIdAccessToken = accessToken1.getUserId();

        if(userIdRefreshToken != userIdAccessToken  ){
            res.put("error_message", "Invalid token code1");
            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
        }

        final Claims claims = jwtProvider.getRefreshClaims(requestRefreshToken);
        final String username = claims.getSubject();

        System.out.println("claims " + claims);
        System.out.println("username " + username);

        final User user = userService.getByUsername(username);

        if (user == null) {
            res.put("error_message", "Not found user ");
            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
        }

        final RefreshToken saveRefreshToken = refreshTokenService.getByUserId(user.getId());

        if (saveRefreshToken != null && saveRefreshToken.getToken().equals(requestRefreshToken)) {

            final JwtProvider.TokenResult accessToken = jwtProvider.generateAccessToken(user);
            final JwtProvider.TokenResult refreshToken = jwtProvider.generateRefreshToken(user);

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());

            RefreshToken rt = new RefreshToken();

            rt.setUserId(user.getId());
            rt.setToken(refreshToken.token);
            rt.setCreatedAt(currentTimestamp);
            rt.setExpiredAt(new Timestamp(refreshToken.expiration.getTime()));

            int rid = this.refreshTokenService.insert(rt);

            if(rid == 0){
                res.put("error_message", "refreshToken id null");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }
            //stugel rid ete 0 error

            AccessToken at = new AccessToken();

            at.setUserId(user.getId());
            at.setToken(refreshToken.token);
            at.setCreatedAt(currentTimestamp);
            at.setExpiredAt(new Timestamp(refreshToken.expiration.getTime()));
            at.setRefreshTokenId(rid);

            int aid = this.accessTokenService.insert(at);

            if(aid == 0){
                res.put("error_message", "accessToken id null");
                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
            }

            //stugel aid ete 0 error

            res.put("accessToken ", accessToken.getToken());
            res.put("accessToken expiration ", accessToken.getExpiration());
            res.put("refreshToken", refreshToken.getToken());
            res.put("refreshToken expiration", refreshToken.getExpiration());

            return new ResponseEntity<>(res.toString(), HttpStatus.OK);
        }
        res.put("error_message", "Invalid  token code2 ");
        return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
    }


//    public JwtAuthentication getAuthInfo() {
//        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
//    }
}
//  public ResponseEntity<String> refresh(@NonNull String requestRefreshToken, @NonNull String requestAccessToken) throws SQLException {
//        JSONObject res = new JSONObject();
//
//        if (jwtProvider.validateRefreshToken(requestRefreshToken) == false) {
//            res.put("error_message", "Invalid  RefreshToken ");
//            return new ResponseEntity<>(res.toString(), HttpStatus.UNAUTHORIZED);
//        }
//
//        if (jwtProvider.validateAccessToken(requestAccessToken) == false) {
//            res.put("error_message", "Invalid  accessToken ");
//            return new ResponseEntity<>(res.toString(), HttpStatus.UNAUTHORIZED);
//        }
//
//        String rftoken = requestRefreshToken;
//        RefreshToken refreshToken1 = this.refreshTokenService.getByRefreshToken(rftoken);
//        int userIdRefreshToken = refreshToken1.getUserId();
//
//
//        String aToken = requestAccessToken;
//        AccessToken accessToken1 = this.accessTokenService.getByAccessToken(aToken);
//        int userIdAccessToken = accessToken1.getUserId();
//
//        if(userIdRefreshToken != userIdAccessToken  ){
//            res.put("error_message", "Invalid token code1");
//            return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
//        }
//
//        final Claims claims = jwtProvider.getRefreshClaims(requestRefreshToken);
//        final String username = claims.getSubject();
//
//        System.out.println("claims " + claims);
//        System.out.println("username " + username);
//
//        final User user = userService.getByUsername(username);
//
//        if (user == null) {
//            res.put("error_message", "Not found user ");
//            return new ResponseEntity<>(res.toString(), HttpStatus.NOT_FOUND);
//        }
//
//        final RefreshToken saveRefreshToken = refreshTokenService.getByUserId(user.getId());
//
//        if (saveRefreshToken != null && saveRefreshToken.getToken().equals(requestRefreshToken)) {
//
//            final JwtProvider.TokenResult accessToken = jwtProvider.generateAccessToken(user);
//            final JwtProvider.TokenResult refreshToken = jwtProvider.generateRefreshToken(user);
//
//            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
//
//            RefreshToken rt = new RefreshToken();
//
//            rt.setUserId(user.getId());
//            rt.setToken(refreshToken.token);
//            rt.setCreatedAt(currentTimestamp);
//            rt.setExpiredAt(new Timestamp(refreshToken.expiration.getTime()));
//
//            int rid = this.refreshTokenService.insert(rt);
//
//            if(rid == 0){
//                res.put("error_message", "refreshToken id null");
//                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
//            }
//            //stugel rid ete 0 error
//
//            AccessToken at = new AccessToken();
//
//            at.setUserId(user.getId());
//            at.setToken(refreshToken.token);
//            at.setCreatedAt(currentTimestamp);
//            at.setExpiredAt(new Timestamp(refreshToken.expiration.getTime()));
//            at.setRefreshTokenId(rid);
//
//            int aid = this.accessTokenService.insert(at);
//
//            if(aid == 0){
//                res.put("error_message", "accessToken id null");
//                return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
//            }
//
//            //stugel aid ete 0 error
//
//            res.put("accessToken ", accessToken.getToken());
//            res.put("accessToken expiration ", accessToken.getExpiration());
//            res.put("refreshToken", refreshToken.getToken());
//            res.put("refreshToken expiration", refreshToken.getExpiration());
//
//            return new ResponseEntity<>(res.toString(), HttpStatus.OK);
//        }
//        res.put("error_message", "Invalid  token code2 ");
//        return new ResponseEntity<>(res.toString(), HttpStatus.BAD_REQUEST);
//    }