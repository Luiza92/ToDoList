package com.to_do_list_app.service;

import com.to_do_list_app.model.JwtAuthentication;
import com.to_do_list_app.model.Role;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }

    private static List<Role> getRoles(Claims claims) {
        final JSONObject roles = new JSONObject(claims.get("roles", String.class));
        System.out.println("roles  " + claims.getSubject() );
        System.out.println("roles  " + claims );
        return Arrays.asList(new Role(roles));

    }
}
