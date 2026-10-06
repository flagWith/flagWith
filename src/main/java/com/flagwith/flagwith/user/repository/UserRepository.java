package com.flagwith.flagwith.user.repository;


import com.flagwith.flagwith.user.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

}
