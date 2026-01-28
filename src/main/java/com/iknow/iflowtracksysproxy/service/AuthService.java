package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.auth.AuthResponse;
import com.iknow.iflowtracksysproxy.dto.auth.LoginRequest;
import com.iknow.iflowtracksysproxy.dto.auth.RegisterRequest;
import com.iknow.iflowtracksysproxy.entity.User;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.TracksysUsersResponse;
import com.iknow.iflowtracksysproxy.respository.UserRepository;
import com.iknow.iflowtracksysproxy.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MilesService milesService;


    @PostConstruct
    public void initTestUsers() {
        createTestUserIfNotExists("admin@test.com", "Admin User", "123456", User.Role.ADMIN);
        createTestUserIfNotExists("purchasing@test.com", "Purchasing User", "123456", User.Role.PURCHASING_UNIT);
        createTestUserIfNotExists("dealer@test.com", "Dealer User", "123456", User.Role.DEALER);
        createTestUserIfNotExists("advisor@test.com", "Advisor User", "123456", User.Role.ADVISOR);

        log.info("===========================================");
        log.info("TEST USERS CREATED - Use these to login:");
        log.info("-------------------------------------------");
        log.info("ADMIN:     admin@test.com / 123456");
        log.info("PURCHASING: purchasing@test.com / 123456");
        log.info("DEALER:    dealer@test.com / 123456");
        log.info("ADVISOR:   advisor@test.com / 123456");
        log.info("===========================================");
    }

    private void createTestUserIfNotExists(String email, String name, String password, User.Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build();
            userRepository.save(user);
            log.info("Created test user: {} with role {}", email, role);
        }
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        TracksysUsersResponse tracksysUsers = milesService.getTracksysUsers();
        var users = tracksysUsers.getData().getTracksysUsersSet().getTracksysUsers();


        var isTracksysUser = false;
        Long foundedTracksysUsersId = null;
        for (var user : users) {
            if (
                    user.getTracksysEmail().equals(request.getEmail())
                    && user.getDescription().equals("Tracksys")
            ){
                isTracksysUser = true;
                foundedTracksysUsersId = user.getUserAccountId();
                break;
            }
        }

        if (!isTracksysUser || foundedTracksysUsersId == null) {
            throw new RuntimeException("User is not tracksys user");
        }

        User user = User.builder()
                .id(foundedTracksysUsersId)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", savedUser.getRole().name());
        String token = jwtUtil.generateToken(savedUser.getEmail(), claims);

        return AuthResponse.builder()
                .token(token)
                .user(toUserDto(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        String token = jwtUtil.generateToken(user.getEmail(), claims);

        return AuthResponse.builder()
                .token(token)
                .user(toUserDto(user))
                .build();
    }

    public AuthResponse.UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toUserDto(user);
    }

    private AuthResponse.UserDto toUserDto(User user) {
        return AuthResponse.UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
