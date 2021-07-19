package com.aragh.resource.filter;

import com.aragh.resource.model.Role;
import com.aragh.resource.model.User;
import com.aragh.resource.utils.PemUtils;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
            super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            try {

                URL publicKey = Thread.currentThread().getContextClassLoader().getResource("public_key.pem");
                assert publicKey != null;

                KeyPair keyPair = PemUtils.getKeyPair(null, Paths.get(publicKey.toURI()), "RSA");

                // parse the token.
                Jwt jwt = Jwts.parser()
                        .setSigningKey(keyPair.getPublic())
                        .parse(token.split(" ")[1]);

                DefaultClaims defaultClaims = (DefaultClaims) jwt.getBody();
                User user = new User();
                user.setUsername(defaultClaims.getSubject());
                List<String> roles = (List<String>) defaultClaims.getOrDefault("roles", Arrays.asList(Role.READ_PRIVILEDGE.getAuthority()));
                user.setRoles(roles.stream().map(Role::valueOf).collect(Collectors.toList()));
                return new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
