package com.musicbackend.Controller;

import com.musicbackend.JWTModel.JWTRequest;
import com.musicbackend.JWTModel.JWTResponse;
import com.musicbackend.Security.JWTHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JWTHelper jwtHelper;

    private Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JWTRequest request) {
        this.doAuthenticate(request.getEmail(),request.getPassword());
        UserDetails userDetails=this.userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.jwtHelper.generateToken(userDetails);
        JWTResponse response=JWTResponse
                .builder()
                .token(token)
                .username(userDetails.getUsername())
                .build();
        return ResponseEntity.ok(response);
    }

    private void doAuthenticate(String email,String password){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email,password);
        try{
            manager.authenticate(authenticationToken);
        }
        catch(BadCredentialsException e){
            throw new BadCredentialsException("invalid Username or Password");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    private String exceptionHandler() {
        return "Invalid Credentials";
    }
}
