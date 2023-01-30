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
import java.io.IOException;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {


    @Autowired
    private AccessTokenService accessTokenService;

    private static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
            throws IOException, ServletException {

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


            AccessToken accessToken1 = null;
            try {
                accessToken1 = this.accessTokenService.getByAccessToken(token);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //         int userId = accessToken1.getUserId();


//            try {
//                User user = this.userService.getByUserId(userId);
//
//                if(user.getRoleId() != 1 ){
//                    System.out.println("role error ");
//                }
//
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }


            // User get from service by token

            // user.getRoleId() == 1 => true

            fc.doFilter(request, response);

            return;
        }

        response.setContentType("application/json");
        res.put("error_message", "invalid accessToken");

        response.getWriter().print(res.toString());
        response.flushBuffer();

    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);

        }
        return null;
    }


}