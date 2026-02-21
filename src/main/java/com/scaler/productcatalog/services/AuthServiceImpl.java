package com.scaler.productcatalog.services;

import com.scaler.productcatalog.exceptions.UserAlreadyExistsException;
import com.scaler.productcatalog.exceptions.UserNotFoundException;
import com.scaler.productcatalog.models.Role;
import com.scaler.productcatalog.models.Session;
import com.scaler.productcatalog.models.User;
import com.scaler.productcatalog.repositories.RoleRepository;
import com.scaler.productcatalog.repositories.SessionRepository;
import com.scaler.productcatalog.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService{

    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    SecretKey secretKey;

    @Override
    public Pair<User, String> login(String email, String password) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found with email: " + email);
        }
        if(!encoder.matches(password, user.get().getPassword())){
            throw new UserNotFoundException("Invalid password");
        }

        String token = Jwts.builder()
                .subject(user.get().getEmail())
                .claim("userId", user.get().getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(secretKey)
                .compact();

        Session session = new Session();
        session.setToken(token);
        session.setUser(user.get());
        session.setCreatedAt(new Date());
        session.setUpdatedAt(new Date());
        session.setIsActive(true);

        sessionRepository.save(session);

        return new Pair<>(user.get(), token);
    }

    @Override
    public User register(String email, String password, String firstName, String lastName)
        throws UserAlreadyExistsException {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            throw new UserAlreadyExistsException("User already exists with email: " + email);
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(password));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        Role role;
        Optional<Role> roleOptional = roleRepository.findByValue(ROLE_USER);
        if (roleOptional.isEmpty()){
            role = new Role();
            role.setValue(ROLE_USER);
            role.setIsActive(true);
            role.setCreatedAt(new Date());
            roleRepository.save(role);
        } else {
            role = roleOptional.get();
        }
        newUser.getRoles().add(role);
        newUser.setIsActive(true);
        newUser.setCreatedAt(new Date());
        newUser.setUpdatedAt(new Date());
        return userRepository.save(newUser);
    }

    @Override
    public User validateToken(String token) throws UserNotFoundException {
        Optional<Session> session = sessionRepository.findByToken(token);
        if(session.isEmpty()){
            throw new UserNotFoundException("Invalid token");
        }

        Session sessionObject = session.get();
        if (Boolean.FALSE.equals(sessionObject.getIsActive())) {
            throw new UserNotFoundException("Session is inactive");
        }

        JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = parser.parseSignedClaims(token).getPayload();
        String userId = claims.get("userId").toString();
        Date exp = claims.getExpiration();
        Date now = new Date();
        if(now.after(exp)){
            throw new UserNotFoundException("Token expired");
        }
        User user = sessionObject.getUser();
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UserNotFoundException("User is inactive");
        }
        if(!user.getId().equals(Long.parseLong(userId))){
            throw new UserNotFoundException("Invalid token");
        }
        return user;
    }
}
