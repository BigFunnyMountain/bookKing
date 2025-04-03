package xyz.tomorrowlearncamp.bookking.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.bookking.user.auth.dto.SignupRequest;
import xyz.tomorrowlearncamp.bookking.user.auth.dto.SignupResponse;
import xyz.tomorrowlearncamp.bookking.user.repository.UserRepository;
import xyz.tomorrowlearncamp.bookking.user.entity.User;
import xyz.tomorrowlearncamp.bookking.user.enums.UserRole;

/**
 * 작성자 : 문성준
 * 일시 : 2025.04.03 - v1
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



}
