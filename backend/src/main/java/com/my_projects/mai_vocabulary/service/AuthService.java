package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.AuthResponse;
import com.my_projects.mai_vocabulary.entity.User;
import com.my_projects.mai_vocabulary.repository.UserRepository;
import com.my_projects.mai_vocabulary.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(
            String email,
            String password) throws Exception{

        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        if(!authentication.isAuthenticated()) {

            throw new Exception("User not found");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        String token =
                jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);

    }
}
