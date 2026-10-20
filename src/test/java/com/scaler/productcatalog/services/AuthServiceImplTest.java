package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.InvalidCredentialsException;
import com.scaler.productcatalog.exceptions.InvalidTokenException;
import com.scaler.productcatalog.exceptions.UserAlreadyExistsException;
import com.scaler.productcatalog.models.Role;
import com.scaler.productcatalog.models.Session;
import com.scaler.productcatalog.models.User;
import com.scaler.productcatalog.repositories.RoleRepository;
import com.scaler.productcatalog.repositories.SessionRepository;
import com.scaler.productcatalog.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode("VGhpcy1pcy1hLWRlbW8tc2VjcmV0LWtleS1jaGFuZ2UtbWUtbm93LTEyMzQ="));
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private SessionRepository sessionRepository;
    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "encoder", encoder);
        ReflectionTestUtils.setField(authService, "secretKey", KEY);
        ReflectionTestUtils.setField(authService, "expiryMinutes", 10L);
    }

    private User user(long id, String rawPassword) {
        User user = new User();
        user.setId(id);
        user.setEmail("jane@example.com");
        user.setPassword(encoder.encode(rawPassword));
        user.setIsActive(true);
        return user;
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(new User()));
        assertThatThrownBy(() -> authService.register("jane@example.com", "password1", "Jane", "Doe"))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void registerHashesPasswordAndAssignsDefaultRole() {
        Role role = new Role();
        role.setValue(Role.NON_ADMIN_USER);
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByValue(Role.NON_ADMIN_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = authService.register("jane@example.com", "password1", "Jane", "Doe");

        assertThat(saved.getPassword()).isNotEqualTo("password1");
        assertThat(encoder.matches("password1", saved.getPassword())).isTrue();
        assertThat(saved.getRoles()).extracting(Role::getValue).containsExactly(Role.NON_ADMIN_USER);
    }

    @Test
    void loginUsesSameErrorForUnknownEmailAndWrongPassword() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user(1, "password1")));

        assertThatThrownBy(() -> authService.login("nobody@example.com", "x"))
                .isInstanceOf(InvalidCredentialsException.class).hasMessage("Invalid email or password");
        assertThatThrownBy(() -> authService.login("jane@example.com", "wrong"))
                .isInstanceOf(InvalidCredentialsException.class).hasMessage("Invalid email or password");
    }

    @Test
    void loginIssuesSignedTokenAndStoresSession() {
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user(7, "password1")));

        LoginResult result = authService.login("jane@example.com", "password1");

        var claims = Jwts.parser().verifyWith(KEY).build().parseSignedClaims(result.token()).getPayload();
        assertThat(claims.getSubject()).isEqualTo("jane@example.com");
        assertThat(claims.get("userId", Long.class)).isEqualTo(7L);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void validateTokenRejectsExpiredTokenAndDeactivatesSession() {
        User user = user(7, "password1");
        String expired = Jwts.builder().subject(user.getEmail()).claim("userId", 7L)
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(KEY).compact();
        Session session = new Session();
        session.setToken(expired);
        session.setUser(user);
        session.setIsActive(true);
        when(sessionRepository.findByToken(expired)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> authService.validateToken(expired)).isInstanceOf(InvalidTokenException.class);
        assertThat(session.getIsActive()).isFalse();
    }

    @Test
    void validateTokenRejectsUnknownToken() {
        when(sessionRepository.findByToken("abc")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.validateToken("abc")).isInstanceOf(InvalidTokenException.class);
    }
}
