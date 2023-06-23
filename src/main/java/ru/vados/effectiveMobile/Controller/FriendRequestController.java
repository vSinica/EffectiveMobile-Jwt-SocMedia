package ru.vados.effectiveMobile.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.vados.effectiveMobile.Entity.UserEntity;
import ru.vados.effectiveMobile.Service.FriendshipRequestServiceImpl;
import ru.vados.effectiveMobile.Config.UserDetailService;

@RestController
@RequestMapping("/api/friend-requests")
@AllArgsConstructor
//@Api("FriendshipRequestController")
public class FriendRequestController {

    private final FriendshipRequestServiceImpl friendshipRequestService;
    private final UserDetailService userDetailService;


    @PostMapping("/send/{receiverId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> sendFriendRequest(
            Authentication authentication,
            @PathVariable Long receiverId
    ) {
        String senderUsername = authentication.getName();
        return friendshipRequestService.sendFriendRequest(senderUsername, receiverId);

    }

    @PostMapping("/accept/{requestId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> acceptFriendRequest( Authentication authentication,
                                                      @PathVariable Long requestId) {

        String receiverUsername = authentication.getName();
        return friendshipRequestService.acceptFriendRequest(requestId, receiverUsername);
    }

    @PostMapping("/reject/{requestId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> rejectFriendRequest(Authentication authentication,
                                                    @PathVariable Long requestId) {

        String receiverUsername = authentication.getName();
        friendshipRequestService.rejectFriendRequest((UserDetails) authentication.getDetails(), requestId);

        return ResponseEntity.ok("Friendship request reject.");
    }

    @PostMapping("/cancel/{receiverId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> cancelFriendRequest( Authentication authentication,
                                                      @PathVariable Long receiverId) {

        String senderUsername = authentication.getName();
        UserEntity sender = userDetailService.getUserByUsername(senderUsername);
        UserEntity receiver = userDetailService.getUserById(receiverId).orElseThrow();

        friendshipRequestService.cancelFriendRequest(sender, receiver);

        return ResponseEntity.ok("Friend request cancelled successfully");
    }

    @DeleteMapping("/{friendId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> removeFriend( Authentication authentication,
                                               @PathVariable Long friendId) {

        String currentUserUsername = authentication.getName();
        UserEntity currentUser = userDetailService.getUserByUsername(currentUserUsername);
        UserEntity friendToRemove = userDetailService.getUserById(friendId).orElse(null);

        if (friendToRemove == null) {
            return ResponseEntity.notFound().build();
        }

        friendshipRequestService.removeFriend(currentUser, friendToRemove);

        return ResponseEntity.ok("Friend removed successfully");
    }
}