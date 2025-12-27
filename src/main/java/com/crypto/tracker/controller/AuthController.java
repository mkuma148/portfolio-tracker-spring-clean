package com.crypto.tracker.controller;

import com.crypto.tracker.auth.util.JwtUtil;
import com.crypto.tracker.model.User;
import com.crypto.tracker.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "https://portfolio-tracker-react-production-4f59.up.railway.app"})
public class AuthController {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private final GoogleIdTokenVerifier verifier;

  public AuthController(UserRepository userRepository, JwtUtil jwtUtil,
                        @Value("${app.google.client-id}") String googleClientId) {
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
    this.verifier = new GoogleIdTokenVerifier
        .Builder(new com.google.api.client.http.javanet.NetHttpTransport(),
        		GsonFactory.getDefaultInstance())
        .setAudience(Collections.singletonList(googleClientId))
        .build();
  }

  // manual register
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
    String email = body.get("email");
    String password = body.get("password");
    String firstName = body.get("firstName");
    String lastName = body.get("lastName");

    if (email == null || password == null) {
      return ResponseEntity.badRequest().body("email & password required");
    }

    Optional<User> existing = userRepository.findByEmail(email);
    if (existing.isPresent()) {
      User u = existing.get();
      if ("GOOGLE".equals(u.getProvider())) {
        return ResponseEntity.badRequest().body("Email already registered with Google. Use Google login.");
      } else {
        return ResponseEntity.badRequest().body("Email already registered. Please login.");
      }
    }

    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setProvider("LOCAL");
    user.setName(firstName+" "+lastName);
    userRepository.save(user);
    
    HashMap<String, Object> claims = new HashMap<>();
    claims.put("role", "USER");
    claims.put("name", user.getName());
    claims.put("email", user.getEmail());
    claims.put("picture", user.getAvatar());

    String token = jwtUtil.generateToken(user.getEmail(), claims);
    return ResponseEntity.ok(Map.of("token", token));
  }

  // manual login
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
    String email = body.get("email");
    String password = body.get("password");

    Optional<User> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
    User user = userOpt.get();

    if ("GOOGLE".equals(user.getProvider())) {
      return ResponseEntity.badRequest().body("This email belongs to Google login. Please login with Google.");
    }

    if (!passwordEncoder.matches(password, user.getPassword())) {
      return ResponseEntity.status(401).body("Invalid credentials");
    }
    
    HashMap<String, Object> claims = new HashMap<>();
    claims.put("role", "USER");
    claims.put("name", user.getName());
    claims.put("email", user.getEmail());
    claims.put("picture", user.getAvatar());

    String token = jwtUtil.generateToken(user.getEmail(), claims);
    return ResponseEntity.ok(Map.of("token", token));
  }

  // Google login: frontend sends idToken (Google ID Token)
  @PostMapping("/google")
  public ResponseEntity<?> googleLogin(@RequestBody Map<String,String> body) throws Exception {
    String idTokenString = body.get("idToken");

    GoogleIdToken idToken = verifier.verify(idTokenString);
    if (idToken == null) {
      return ResponseEntity.status(401).body("Invalid ID token");
    }

    GoogleIdToken.Payload payload = idToken.getPayload();
    String email = payload.getEmail();
    String name = (String) payload.get("name");
    String picture = (String) payload.get("picture");
    String sub = payload.getSubject(); // google unique id

    // find existing user by email
    Optional<User> userOpt = userRepository.findByEmail(email);
    User user;
    if (userOpt.isPresent()) {
      user = userOpt.get();
      // If existing with LOCAL provider, you may either reject or link accounts.
      if ("LOCAL".equals(user.getProvider())) {
        // Option A: reject and ask user to use local login
        // return ResponseEntity.badRequest().body("Email already registered via local signup");
        // Option B: link accounts (set provider GOOGLE and providerId) - decision business logic
        // Here we'll prefer linking if providerId empty:
        user.setProvider("GOOGLE");
        user.setProviderId(sub);
        user.setAvatar(picture);
        userRepository.save(user);
      }
    } else {
      // create new user
      user = new User();
      user.setEmail(email);
      user.setProvider("GOOGLE");
      user.setProviderId(sub);
      user.setName(name);
      user.setAvatar(picture);
      userRepository.save(user);
    }
    
    HashMap<String, Object> claims = new HashMap<>();
    claims.put("role", "USER");
    claims.put("name", user.getName());
    claims.put("email", user.getEmail());
    claims.put("picture", user.getAvatar());

    String token = jwtUtil.generateToken(user.getEmail(), claims);
    return ResponseEntity.ok(Map.of("token", token));
  }
}

