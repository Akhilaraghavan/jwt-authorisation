package com.aragh.resource.filter;

import com.aragh.resource.config.WebSecurity;
import com.aragh.resource.model.User;
import com.aragh.resource.utils.PemUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.ArrayList;

import static io.jsonwebtoken.SignatureAlgorithm.RS256;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(WebSecurity.LOGIN_URL + "/login");
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User creds = new ObjectMapper()
                    .readValue(request.getInputStream(), User.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetails user = (UserDetails) authResult.getPrincipal();
        Object[] array = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray();

        try {

            URL privateKey = Thread.currentThread().getContextClassLoader().getResource("private_key.pem");

            assert privateKey != null;

            KeyPair keyPair = PemUtils.getKeyPair(Paths.get(privateKey.toURI()), null, "RSA");

            String token = Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("roles", array)
                    .signWith(RS256, keyPair.getPrivate()).compact();

            response.getWriter().write(token);
            response.getWriter().flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
