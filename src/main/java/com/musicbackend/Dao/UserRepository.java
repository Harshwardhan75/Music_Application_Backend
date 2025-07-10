package com.musicbackend.Dao;

import com.musicbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String username);
}
