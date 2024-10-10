package com.example.demo.controller;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations for user management")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @PostMapping("/sign-up")
    @Operation(summary = "Create user", description = "Create a user")
    public ResponseEntity<?> singUp(@Valid @RequestBody UserRequest userRequest) throws Exception {

        UserResponse userResponse = userService.createUser(userRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/login")
    @Operation(summary = "Get user",
            description = "Get a user",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> login() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            String tokenValue = jwt.getTokenValue();

            UserResponse userResponse = userService.getUser(tokenValue);
            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token not found", HttpStatus.UNAUTHORIZED);
        }
    }
}

