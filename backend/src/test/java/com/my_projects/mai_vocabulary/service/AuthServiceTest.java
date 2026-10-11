package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.AuthResponse;
import com.my_projects.mai_vocabulary.dto.LoginRequest;
import com.my_projects.mai_vocabulary.entity.User;
import com.my_projects.mai_vocabulary.repository.UserRepository;
import com.my_projects.mai_vocabulary.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.postgresql.hostchooser.HostRequirement.any;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId(1L);
//        user.setEmail("test@example.com");
//    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        when(jwtService.generateToken(user.getEmail()))
                .thenReturn("jwt-token");

        when(authenticationManager.authenticate(any()))
                .thenReturn(
                        UsernamePasswordAuthenticationToken.authenticated(
                                "test@example.com",
                                null,
                                List.of(new SimpleGrantedAuthority("USER"))
                        )
                );

        AuthResponse response =
                authService.login(request.getEmail(), request.getPassword());

        assertThat(response.getToken())
                .isEqualTo("jwt-token");
    }
}
