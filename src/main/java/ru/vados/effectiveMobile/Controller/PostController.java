package ru.vados.effectiveMobile.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.vados.effectiveMobile.Config.UserPrincipal;
import ru.vados.effectiveMobile.Dto.PostDto;
import ru.vados.effectiveMobile.Service.PostService;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PutMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> createPost(@RequestBody PostDto.PostCreate post, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to create a post.");
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        postService.createPost(post, userPrincipal.getUsername());
        return ResponseEntity.ok("Post created successfully.");
    }

    @GetMapping("/{postId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> getPostById(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to view the post.");
        }

        PostDto postDto = postService.getPostById(postId);
        if (postDto != null) {
            return ResponseEntity.ok(postDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping()
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> updatePost(@RequestBody PostDto.PostUpdate updatedPost, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to update the post.");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String username = userPrincipal.getUsername();

        boolean isPostBelongsToUser = postService.isPostBelongsToUser(updatedPost.getId(), username);
        if (!isPostBelongsToUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to update this post.");
        }

        boolean success = postService.updatePost(updatedPost);
        if (success) {
            return ResponseEntity.ok("Post updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{postId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete the post.");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String username = userPrincipal.getUsername();

        boolean isPostBelongsToUser = postService.isPostBelongsToUser(postId, username);
        if (!isPostBelongsToUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to delete this post.");
        }

        boolean success = postService.deletePost(postId);
        if (success) {
            return ResponseEntity.ok("Post deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}