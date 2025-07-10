package com.musicbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;

    @Size(min = 3, max = 30)
    @NotBlank(message = "Username is Required")
    private String username;

    @Email(message = "Enter a valid Email Address")
    @NotBlank(message = "Email is Required")
    private String email;

    @Size(min = 8, max = 100)
    @NotBlank(message = "Password Cannot be Blank")
    private String password;

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Song> songs;
}
