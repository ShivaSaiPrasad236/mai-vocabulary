package com.my_projects.mai_vocabulary.controller;

import com.my_projects.mai_vocabulary.dto.RegisterRequest;
import com.my_projects.mai_vocabulary.entity.User;
import com.my_projects.mai_vocabulary.service.AuthService;
import com.my_projects.mai_vocabulary.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldRegisterUser() throws Exception {

        User user = new User();
        user.setEmail("john@example.com");
        user.setName("John");
        user.setPassword("password123");

        RegisterRequest req = new RegisterRequest();
        req.setEmail("name@email.com");
        req.setName("name");
        req.setPassword("pass");

        when(userService.register("name", "name@email.com", "pass"))
                .thenReturn(user);

        ResponseEntity res = authController.register(req);
        assert(res.getStatusCode()).equals(HttpStatus.CREATED);

    }


}
