package com.flagwith.flagwith.user.service;

import com.flagwith.flagwith.user.User;
import com.flagwith.flagwith.user.dto.response.UserResponse;
import com.flagwith.flagwith.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }
}
