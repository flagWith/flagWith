package com.flagwith.flagwith.sign_up.repository;

import com.flagwith.flagwith.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignUpRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

}
