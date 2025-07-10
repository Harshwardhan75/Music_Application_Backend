package com.musicbackend.Helper;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PayLoad {
    private String message;
    private String status;
    private LocalDateTime localDateTime;
    private Object data;
}
