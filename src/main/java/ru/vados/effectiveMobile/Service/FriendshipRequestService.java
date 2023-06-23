package ru.vados.effectiveMobile.Service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import ru.vados.effectiveMobile.Entity.UserEntity;

import java.util.Optional;

public interface FriendshipRequestService {
    ResponseEntity sendFriendRequest(String senderUsername, Long receiverId);
    ResponseEntity acceptFriendRequest(Long requestId, String receiverUsername);

    void rejectFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Long requestId);

    ResponseEntity cancelFriendRequest(UserEntity sender, UserEntity receiver);

}
