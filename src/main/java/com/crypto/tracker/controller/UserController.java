package com.crypto.tracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crypto.tracker.dto.request.RegistrationRequest;
import com.crypto.tracker.dto.response.RegistrationResponse;
import com.crypto.tracker.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "https://portfolio-tracker-react-production.up.railway.app"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<RegistrationResponse> signup(@RequestBody RegistrationRequest request) {
        Long id = userService.signup(request.getUsername(), request.getPassword(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrationResponse(id, "User registered successfully"));
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        boolean success = userService.login(username, password);
        return success ? "Login successful" : "Invalid username/password";
    }
}