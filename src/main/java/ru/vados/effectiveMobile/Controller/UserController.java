package ru.vados.effectiveMobile.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.vados.effectiveMobile.Config.JwtUtil;
import ru.vados.effectiveMobile.Dto.PostDto;
import ru.vados.effectiveMobile.Dto.UserDto;
import ru.vados.effectiveMobile.Service.PostService;
import ru.vados.effectiveMobile.Config.UserDetailService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto.UserRegister user) {
        userDetailService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDto.UserLogin user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<PostDto>> getAllPosts(@PathVariable Long userId,  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<PostDto> postDTOs = postService.getAllPostsByUserId(userId);
        return ResponseEntity.ok(postDTOs);
    }

    @GetMapping("/{userId}/feed")
    public ResponseEntity<List<PostDto>> getActivityFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        List<PostDto> activityFeed = postService.getActivityFeed(userId, page, pageSize);
        return ResponseEntity.ok(activityFeed);
    }
}