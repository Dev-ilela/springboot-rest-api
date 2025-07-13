package com.gbm.challenge.service;

import com.gbm.challenge.dto.auth.LoginRequestDTO;
import com.gbm.challenge.dto.auth.LoginResponseDTO;
import com.gbm.challenge.dto.auth.RefreshRequestDTO;
import com.gbm.challenge.dto.user.UserRequestDTO;
import com.gbm.challenge.dto.user.UserResponseDTO;
import com.gbm.challenge.model.User;
import com.gbm.challenge.repository.UserRepository;
import com.gbm.challenge.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDTO register(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já está em uso");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User saved = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());

        return response;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            var auth = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
            authManager.authenticate(auth);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public LoginResponseDTO refresh(RefreshRequestDTO dto) {
        String refreshToken = dto.getRefreshToken();

        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        String email = jwtUtil.getSubject(refreshToken);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("Refresh token não confere com o registrado");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email);
        return new LoginResponseDTO(newAccessToken, refreshToken);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

}
