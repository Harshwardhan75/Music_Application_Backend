package com.musicbackend.Controller;

import com.musicbackend.Dao.UserRepository;
import com.musicbackend.Entity.User;
import com.musicbackend.Helper.PayLoad;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public ResponseEntity<?> wellcome(){
        return ResponseEntity.ok(
                new PayLoad("Wellcome","Success", LocalDateTime.now(),null)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result){
        try{
            if(result.hasErrors()){
                Map<String,String> errorResult=new HashMap<>();
                for(var e: result.getFieldErrors()){
                    errorResult.put(e.getField(),e.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(
                    new PayLoad("Validation failed","Success",
                            LocalDateTime.now(),errorResult)
                );
            }


            user.setPassword(this.passwordEncoder.encode(user.getPassword()));
            this.userRepository.save(user);
            return ResponseEntity.ok().body(
                    new PayLoad("User Created Successfully", "Success", LocalDateTime.now(), user)
            );
        }catch(DataIntegrityViolationException e){
            return ResponseEntity.internalServerError().body(
                    new PayLoad("This Email is already in Use", "Failure", LocalDateTime.now(), null)
            );
        }
    }
}
