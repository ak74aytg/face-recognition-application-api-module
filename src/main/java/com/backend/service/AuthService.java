package com.backend.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.backend.request.LoginRequest;
import com.backend.response.AuthResponse;
import com.backend.security.JwtProvider;

@Service
public class AuthService {

	@Autowired
	Cloudinary cloudinary;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	public AuthResponse RegisterUser(User user, MultipartFile image) throws Exception{
		User savedUser = userRepository.findByEmail(user.getEmail());
		if(savedUser!=null) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("status", "failed");
        	map.put("message", "User already exist");
            return new AuthResponse(null, map);
		}
		user.setId(UUID.randomUUID().toString());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if(image!=null && !image.isEmpty()) {
			Map options = ObjectUtils.asMap(
					"folder", "profile-images"
			);
			Map uploadResult = cloudinary.uploader().upload(image.getBytes(), options);
			// Save image URL and other data
			user.setProfile_url((String) uploadResult.get("secure_url")); // Use secure URL for HTTPS
		}
		savedUser = userRepository.save(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        String token = JwtProvider.generateToken(authentication);
        HashMap<String, Object> map = new HashMap<>();
    	map.put("message", "Registered successfully");
		map.put("status", "success");
		User loggedInUser = new User();
		loggedInUser.setId(user.getId());
		loggedInUser.setEmail(user.getEmail());
		loggedInUser.setName(user.getEmail());
		loggedInUser.setProfile_url(user.getProfile_url());
		loggedInUser.setPincode(user.getPincode());
		loggedInUser.setLocation(user.getLocation());
		loggedInUser.setToken(user.getToken());
		map.put("user", loggedInUser);
        return new AuthResponse(token, map);
	}
	
	
	public AuthResponse loginUser(LoginRequest user) throws Exception {
		Authentication authentication = authenticate(user.getEmail(), user.getPassword());
        String token = JwtProvider.generateToken(authentication);
        String email = user.getEmail();
        User savedUser = userRepository.findByEmail(email);
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "login successfull");
        User loggedInUser = new User();
		loggedInUser.setId(savedUser.getId());
        loggedInUser.setEmail(savedUser.getEmail());
        loggedInUser.setName(savedUser.getName());
        loggedInUser.setProfile_url(savedUser.getProfile_url());
		loggedInUser.setToken(savedUser.getToken());
		loggedInUser.setLocation(savedUser.getLocation());
		loggedInUser.setPincode(savedUser.getPincode());
        map.put("user", loggedInUser);
        return new AuthResponse(token, map);
	}
	
	
	private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        if(userDetails==null) {
            throw new BadCredentialsException("username does not exist!");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("wrong password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
    }

}
