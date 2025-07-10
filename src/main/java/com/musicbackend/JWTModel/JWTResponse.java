package com.musicbackend.JWTModel;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JWTResponse {

    private String username;
    private String token;

}
