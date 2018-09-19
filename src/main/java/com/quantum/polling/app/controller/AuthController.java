package com.quantum.polling.app.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.quantum.polling.app.exception.AppException;
import com.quantum.polling.app.model.Role;
import com.quantum.polling.app.model.RoleName;
import com.quantum.polling.app.model.User;
import com.quantum.polling.app.payload.ApiResponse;
import com.quantum.polling.app.payload.JwtAuthenticationResponse;
import com.quantum.polling.app.payload.LoginRequest;
import com.quantum.polling.app.payload.SignUpRequest;
import com.quantum.polling.app.repository.RoleRepository;
import com.quantum.polling.app.repository.UserRepository;
import com.quantum.polling.app.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
    	
    	Authentication authentication = authenticationManager.authenticate(
    			new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
    	
    	SecurityContextHolder.getContext().setAuthentication(authentication);
    	
    	String jwt = tokenProvider.generateToken(authentication);
    	return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    	
    	if(userRepository.existsByUsername(signUpRequest.getUsername())) {
    		return new ResponseEntity(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	if(userRepository.existsByUsername(signUpRequest.getEmail())) {
    		return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
    	}
    	
    	//Create the user account if the username or email doesn't exist
    	User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
    	
    	Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User Role not set."));
    	
    	user.setRoles(Collections.singleton(userRole));
    	
    	User userResult = userRepository.save(user);
    	
    	URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}").buildAndExpand(userResult.getUsername()).toUri();
    	
    	return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

}
