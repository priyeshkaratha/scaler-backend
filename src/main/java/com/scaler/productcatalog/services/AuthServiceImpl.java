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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService{

    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SecretKey secretKey;

    @Value("${jwt.expiry-minutes:10}")
    private long expiryMinutes;

    @Override
    public LoginResult login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        // Same message for unknown email and wrong password so accounts cannot be enumerated
        if (user.isEmpty() || !encoder.matches(password, user.get().getPassword())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        if (Boolean.FALSE.equals(user.get().getIsActive())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        Date now = new Date();
        String token = Jwts.builder()
                // jti keeps tokens unique even when one user logs in twice within the same second
                .id(UUID.randomUUID().toString())
                .subject(user.get().getEmail())
                .claim("userId", user.get().getId())
                .claim("roles", user.get().getRoles().stream().map(Role::getValue).toList())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiryMinutes * 60 * 1000))
                .signWith(secretKey)
                .compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user.get());
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        session.setIsActive(true);
        sessionRepository.save(session);

        return new LoginResult(user.get(), token);
    }

    @Override
    @Transactional
    public User register(String email, String password, String firstName, String lastName) {
        return createUser(email, password, firstName, lastName, List.of(Role.NON_ADMIN_USER));
    }

    @Override
    @Transactional
    public User registerAdmin(String email, String password) {
        return createUser(email, password, "Catalog", "Admin", List.of(Role.ADMIN, Role.NON_ADMIN_USER));
    }

    private User createUser(String email, String password, String firstName, String lastName, List<String> roleValues) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with email: " + email);
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(password));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        for (String value : roleValues) {
            newUser.getRoles().add(findOrCreateRole(value));
        }
        newUser.setIsActive(true);
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());
        return userRepository.save(newUser);
    }

    private Role findOrCreateRole(String value) {
        return roleRepository.findByValue(value).orElseGet(() -> {
            Role role = new Role();
            role.setValue(value);
            role.setIsActive(true);
            role.setCreatedAt(new Date());
            role.setUpdatedAt(new Date());
            return roleRepository.save(role);
        });
    }

    @Override
    public User validateToken(String token) {
        Session session = sessionRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        if (Boolean.FALSE.equals(session.getIsActive())) {
            throw new InvalidTokenException("Session is inactive");
        }

        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            // Covers expired, tampered and malformed tokens
            deactivate(session);
            throw new InvalidTokenException("Invalid or expired token");
        }

        User user = session.getUser();
        if (Boolean.FALSE.equals(user.getIsActive())) {
            deactivate(session);
            throw new InvalidTokenException("User is inactive");
        }
        if (!user.getId().equals(Long.parseLong(claims.get("userId").toString()))) {
            deactivate(session);
            throw new InvalidTokenException("Invalid token");
        }
        return user;
    }

    @Override
    public void logout(String token) {
        Session session = sessionRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        deactivate(session);
    }

    private void deactivate(Session session) {
        session.setIsActive(false);
        session.setUpdatedAt(new Date());
        sessionRepository.save(session);
    }
}
