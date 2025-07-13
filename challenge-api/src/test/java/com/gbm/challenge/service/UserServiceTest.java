package com.gbm.challenge.service;

import com.gbm.challenge.dto.auth.LoginRequestDTO;
import com.gbm.challenge.dto.auth.LoginResponseDTO;
import com.gbm.challenge.dto.auth.RefreshRequestDTO;
import com.gbm.challenge.dto.user.UserRequestDTO;
import com.gbm.challenge.dto.user.UserResponseDTO;
import com.gbm.challenge.model.User;
import com.gbm.challenge.repository.UserRepository;
import com.gbm.challenge.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // given
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Dev Test");
        request.setEmail("dev@example.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("dev@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Dev Test");
        savedUser.setEmail("dev@example.com");
        savedUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponseDTO response = userService.register(request);

        // then
        assertNotNull(response);
        assertEquals("Dev Test", response.getName());
        assertEquals("dev@example.com", response.getEmail());
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        // given
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Duplicate");
        request.setEmail("exists@example.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(new User()));

        // when + then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });

        assertEquals("E-mail já está em uso", exception.getMessage());
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequestDTO request = new LoginRequestDTO("Devilela@gmail.com", "146343");

        User user = new User();
        user.setEmail("Devilela@gmail.com");
        user.setPassword("146343");

        when(userRepository.findByEmail("Devilela@gmail.com")).thenReturn(Optional.of(user));

        when(jwtUtil.generateAccessToken("Devilela@gmail.com")).thenReturn("access-token-fake");
        when(jwtUtil.generateRefreshToken("Devilela@gmail.com")).thenReturn("refresh-token-fake");

        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(Authentication.class));


        LoginResponseDTO response = userService.login(request);

        assertNotNull(response);
        assertEquals("access-token-fake", response.getAccessToken());
        assertEquals("refresh-token-fake", response.getRefreshToken());
    }

    @Test
    void shouldThrowExceptionOnInvalidPassword() {
        LoginRequestDTO request = new LoginRequestDTO("Devilela@gmail.com", "wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Senha inválida"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("notfound@example.com", "123456");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Usuário não encontrado"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }

    @Test
    void shouldRefreshAccessTokenSuccessfully() {
        RefreshRequestDTO request = new RefreshRequestDTO();
        request.setRefreshToken("valid-refresh-token");

        User user = new User();
        user.setEmail("dev@example.com");
        user.setRefreshToken("valid-refresh-token");

        when(jwtUtil.isTokenValid("valid-refresh-token")).thenReturn(true);
        when(jwtUtil.getSubject("valid-refresh-token")).thenReturn("dev@example.com");
        when(userRepository.findByEmail("dev@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken("dev@example.com")).thenReturn("new-access-token");

        var response = userService.refresh(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("valid-refresh-token", response.getRefreshToken());
    }

    @Test
    void shouldThrowExceptionOnInvalidRefreshToken() {
        RefreshRequestDTO request = new RefreshRequestDTO();
        request.setRefreshToken("invalid-token");

        when(jwtUtil.isTokenValid("invalid-token")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.refresh(request);
        });

        assertEquals("Refresh token inválido ou expirado", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfTokenDoesNotMatchStored() {
        RefreshRequestDTO request = new RefreshRequestDTO();
        request.setRefreshToken("some-token");

        User user = new User();
        user.setEmail("dev@example.com");
        user.setRefreshToken("another-token");

        when(jwtUtil.isTokenValid("some-token")).thenReturn(true);
        when(jwtUtil.getSubject("some-token")).thenReturn("dev@example.com");
        when(userRepository.findByEmail("dev@example.com")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.refresh(request);
        });

        assertEquals("Refresh token não confere com o registrado", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfUserNotFoundOnRefresh() {
        RefreshRequestDTO request = new RefreshRequestDTO();
        request.setRefreshToken("valid-token");

        when(jwtUtil.isTokenValid("valid-token")).thenReturn(true);
        when(jwtUtil.getSubject("valid-token")).thenReturn("ghost@example.com");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.refresh(request);
        });

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

}
