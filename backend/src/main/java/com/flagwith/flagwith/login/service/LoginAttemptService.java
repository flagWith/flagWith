package com.flagwith.flagwith.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration FAILURE_WINDOW = Duration.ofMinutes(15);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final StringRedisTemplate redisTemplate;

    // 잠금 키가 Redis에 있으면 현재 잠긴 상태이다.
    public boolean isLocked(String email) {
        Boolean exists = redisTemplate.hasKey(lockKey(email));
        return Boolean.TRUE.equals(exists);
    }
    
    //로그인 실패 횟수를 올림. 5번째 실패면 잠금 키를 만들고, 실패 횟수 키는 삭제
    public void recordFailure(String email) {
        String countKey = failureCountKey(email);

        Long count = redisTemplate.opsForValue().increment(countKey);

        if (count == null) {
            return;
        }

        if (count == 1) {
            redisTemplate.expire(countKey, FAILURE_WINDOW);
        }

        if (count >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set(
                    lockKey(email), "1", LOCK_DURATION
            );

            redisTemplate.delete(countKey);
        }
    }

    public void clearFailures(String email) {
        redisTemplate.delete(failureCountKey(email));
    }

    private String failureCountKey(String email) {
        return "login:fail:" + normalize(email);
    }

    private String lockKey(String email) {
        return "login:lock:" + normalize(email);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

}
