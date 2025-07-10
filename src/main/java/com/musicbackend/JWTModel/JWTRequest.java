package com.musicbackend.JWTModel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JWTRequest {

    private String email;
    private String password;

}
