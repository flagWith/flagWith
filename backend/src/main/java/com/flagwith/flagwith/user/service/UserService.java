package com.flagwith.flagwith.user.service;

import com.flagwith.flagwith.global.exception.BusinessException;
import com.flagwith.flagwith.user.User;
import com.flagwith.flagwith.global.exception.ErrorCode;
import com.flagwith.flagwith.user.dto.response.UserProfileResponse;
import com.flagwith.flagwith.user.dto.response.UserResponse;
import com.flagwith.flagwith.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserProfileResponse getProfile(Long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.AUTH_FAILED));
            return UserProfileResponse.from(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
    }

}
