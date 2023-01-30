package com.to_do_list_app.filter;


import com.to_do_list_app.model.AccessToken;
import com.to_do_list_app.model.JwtAuthentication;
import com.to_do_list_app.model.User;
import com.to_do_list_app.service.AccessTokenService;
import com.to_do_list_app.service.JwtProvider;
import com.to_do_list_app.service.JwtUtils;
import com.to_do_list_app.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {


    @Autowired
    private AccessTokenService accessTokenService;
    @Autowired
    private UserService userService;

    private static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse _response, FilterChain fc)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) _response;

        JSONObject res = new JSONObject();

        String method = ((HttpServletRequest) request).getMethod();
        String path = ((HttpServletRequest) request).getServletPath();

        System.err.println("PATH: " + path);
        System.err.println("METHOD: " + method);

        if (path.startsWith("/api/login") || path.startsWith("/api/registration") || path.startsWith("/api/approve") || path.startsWith("/uploads")) {
            fc.doFilter(request, response);
            return;
        }


        final String token = getTokenFromRequest((HttpServletRequest) request);


        if (token != null && jwtProvider.validateAccessToken(token)) {
            final Claims claims = jwtProvider.getAccessClaims(token);
            final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            System.out.println("token " + token );

       //     String accessToken = token.replace("Bearer ", "");


            if (checkUserApiPermission(token, method, path) == false) {
                sendError(response, "access denied", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            fc.doFilter(request, response);
            return;
        }
        sendError(response, "invalid accessToken", HttpServletResponse.SC_UNAUTHORIZED);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);

        }
        return null;
    }

    private Boolean checkUserApiPermission(String token, String method, String path) {

        //path.startsWith("/api/user") == false && path.equalsIgnoreCase("get") == false

        if (path.startsWith("/api/user") == false ){
            return true;
        }

        AccessToken accessToken1 = null;
        try {
            accessToken1 = this.accessTokenService.getByAccessToken(token);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int userId1 = accessToken1.getUserId();

        User user1 = null;
        try {
            user1 = this.userService.getByUserId(userId1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (user1.getRoleId() != 1) {
            System.out.println("error_message access denied");
            return false;
        }

        return true;
    }


    public static boolean sendError(HttpServletResponse response, String msg, int code){
        try
        {
            JSONObject res = new JSONObject();
            response.setContentType("application/json");
            res.put("error_message", msg);
            response.setStatus(code);
            response.getWriter().print(res);
            response.flushBuffer();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}