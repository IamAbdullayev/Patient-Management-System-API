package com.pm.authenservice.service;

import com.pm.authenservice.dto.LoginRequestDto;
import com.pm.authenservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;

    public Optional<String> authenticate(LoginRequestDto request) {


        Optional<String> token = Optional.empty();
        
        return token;
    };
}
