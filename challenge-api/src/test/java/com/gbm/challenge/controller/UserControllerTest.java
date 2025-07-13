package com.gbm.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbm.challenge.dto.auth.LoginRequestDTO;
import com.gbm.challenge.dto.auth.LoginResponseDTO;
import com.gbm.challenge.dto.auth.RefreshRequestDTO;
import com.gbm.challenge.dto.user.UserRequestDTO;
import com.gbm.challenge.dto.user.UserResponseDTO;
import com.gbm.challenge.model.User;
import com.gbm.challenge.repository.UserRepository;
import com.gbm.challenge.service.UserService;
import com.gbm.challenge.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetails userDetails;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUser() throws Exception {
        UserRequestDTO req = new UserRequestDTO("John Doe", "john@example.com", "pass123");
        UserResponseDTO res = new UserResponseDTO(1L, "John Doe", "john@example.com");

        when(userService.register(any(UserRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/auth/register").with(csrf()).with(user("test@example.com")).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("John Doe")).andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("john@example.com", "pass123");
        LoginResponseDTO res = new LoginResponseDTO("token123", "refresh123");

        when(userService.login(any(LoginRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/auth/login").with(csrf()).with(user("test@example.com")).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").value("token123")).andExpect(jsonPath("$.refreshToken").value("refresh123"));
    }

    @Test
    void shouldRefreshToken() throws Exception {
        RefreshRequestDTO req = new RefreshRequestDTO("refresh123");
        LoginResponseDTO res = new LoginResponseDTO("newAccess", "newRefresh");

        when(userService.refresh(any(RefreshRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/auth/refresh").with(csrf()).with(user("test@example.com")).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").value("newAccess")).andExpect(jsonPath("$.refreshToken").value("newRefresh"));
    }

    @Test
    void shouldReturnCurrentUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userService.findByEmail("john@example.com")).thenReturn(user);

        mockMvc.perform(get("/auth/me").with(user("john@example.com"))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L)).andExpect(jsonPath("$.name").value("John Doe")).andExpect(jsonPath("$.email").value("john@example.com"));
    }

}
