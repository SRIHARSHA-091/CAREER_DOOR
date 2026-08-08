package com.jobportal.services;

import com.jobportal.dto.AuthRequest;
import com.jobportal.dto.AuthResponse;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.entities.Role;
import com.jobportal.entities.User;
import com.jobportal.exceptions.BadRequestException;
import com.jobportal.repositories.UserRepository;
import com.jobportal.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    private final com.jobportal.repositories.OtpTokenRepository otpTokenRepository;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.JOB_SEEKER)
                .isVerified(false)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        log.info("User logged in successfully: {}", user.getEmail());
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank() || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        try {
            String userEmail = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new BadRequestException("User not found"));
                String accessToken = jwtUtil.generateToken(userDetails);
                return AuthResponse.builder()
                        .token(accessToken)
                        .refreshToken(refreshToken)
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build();
            }
        } catch (RuntimeException ex) {
            log.debug("Refresh token rejected", ex);
        }
        throw new BadRequestException("Invalid refresh token");
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        com.jobportal.entities.OtpToken otpToken = com.jobportal.entities.OtpToken.builder()
                .token(otp)
                .email(email)
                .expiryDate(java.time.LocalDateTime.now().plusMinutes(10))
                .isUsed(false)
                .build();

        otpTokenRepository.save(otpToken);
        emailService.sendOtp(email, otp);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        com.jobportal.entities.OtpToken otpToken = otpTokenRepository.findByTokenAndEmailAndIsUsedFalse(otp, email)
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        if (otpToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);
    }
    
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Incorrect old password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
