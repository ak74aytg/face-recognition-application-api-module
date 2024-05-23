package com.backend.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

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
	private UserRepository userRepository;
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Value("${project.image}")
	private String path;
	
	
	
	public AuthResponse RegisterUser(User user, MultipartFile image) throws Exception{
		User savedUser = userRepository.findByEmail(user.getEmail());
		if(savedUser!=null) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("status", "failed");
        	map.put("message", "User already exist");
            AuthResponse res = new AuthResponse(null, map);
            return res;
		}
		user.setId(UUID.randomUUID().toString());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if(image!=null && !image.isEmpty()) {
			String formattedDateTime = LocalDateTime.now().toString().replace(':', '_');
			String fileName = user.getId()+"_"+formattedDateTime+"_"+image.getOriginalFilename();
        	String filePath = path+File.separator+fileName;
        	File f = new File(path);
        	if(!f.exists()) {
        		f.mkdir();
        	}
        	Files.copy(image.getInputStream(), Paths.get(filePath));
        	user.setProfile_url(fileName);
		}
		if(user.getRole().equals("admin")) {
			user.setRole("ROLE_ADMIN");
		}else {
			user.setRole("ROLE_USER");
		}
		savedUser = userRepository.save(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        String token = JwtProvider.generateToken(authentication);
        HashMap<String, Object> map = new HashMap<>();
    	map.put("message", "Registered successfully");
    	map.put("status", "success");
        AuthResponse res = new AuthResponse(token, map);
        return res;
	}
	
	
	public AuthResponse loginUser(LoginRequest user) throws Exception {
		Authentication authentication = authenticate(user.getEmail(), user.getPassword());
        String token = JwtProvider.generateToken(authentication);
        String email = user.getEmail();
        User savedUser = userRepository.findByEmail(email);
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", "login successfull");
        User loggedInUser = new User();
        loggedInUser.setEmail(savedUser.getEmail());
        loggedInUser.setName(savedUser.getEmail());
        loggedInUser.setProfile_url(savedUser.getProfile_url());
        loggedInUser.setRole(savedUser.getRole());
        map.put("user", loggedInUser);
        AuthResponse res = new AuthResponse(token, map);
        return res;
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
