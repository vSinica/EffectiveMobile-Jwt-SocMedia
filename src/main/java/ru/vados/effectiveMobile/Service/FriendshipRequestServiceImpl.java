package ru.vados.effectiveMobile.Service;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.vados.effectiveMobile.Config.UserDetailService;
import ru.vados.effectiveMobile.Entity.FriendRequestEntity;
import ru.vados.effectiveMobile.Entity.RequestStatusEnum;
import ru.vados.effectiveMobile.Entity.UserEntity;
import ru.vados.effectiveMobile.Repository.FriendshipRequestRepository;
import ru.vados.effectiveMobile.Repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendshipRequestServiceImpl implements FriendshipRequestService {

    private final FriendshipRequestRepository friendshipRequestRepository;
    private final UserRepository userRepository;
    private final UserDetailService userDetailService;


    @Transactional
    public ResponseEntity sendFriendRequest(String senderUsername, Long receiverId) {

        Optional<UserEntity> receiverOptional = userRepository.findById(receiverId);

        if (receiverOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserEntity senderEntity = userDetailService.getUserByUsername(senderUsername);
        UserEntity receiverEntity = receiverOptional.get();

        UserEntity sender = userRepository.findByUsername(senderUsername).orElseThrow(()->{
            throw new IllegalArgumentException("Sender  does not exist");
        });
        UserEntity receiver = userRepository.findByUsername(receiverEntity.getUsername()).orElseThrow(()->{
            throw new IllegalArgumentException("receiver does not exist");
        });

        FriendRequestEntity request = new FriendRequestEntity();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(RequestStatusEnum.PENDING);

        sender.getSubscribers().add(receiver);
        userRepository.save(sender);

        friendshipRequestRepository.save(request);

        return ResponseEntity.ok("Friendship request accept.");
    }

    @Override
    @Transactional
    public ResponseEntity acceptFriendRequest(Long requestId, String receiverName) {

        UserEntity receiver = userRepository.findByUsername(receiverName).orElseThrow();
        UserEntity sender = userRepository.findById(requestId).orElseThrow();

        FriendRequestEntity request = friendshipRequestRepository.findBySenderAndReceiver(sender,receiver).orElse(null);

        if (request == null || request.getStatus() != RequestStatusEnum.PENDING) {
            throw new IllegalArgumentException("Invalid request");
        }

        sender = request.getSender();

        receiver.getFriends().add(sender);
        sender.getFriends().add(receiver);

        userRepository.save(receiver);
        userRepository.save(sender);

        request.setStatus(RequestStatusEnum.ACCEPTED);
        friendshipRequestRepository.save(request);

        return ResponseEntity.ok("Friendship request accept.");
    }

    @Transactional
    public void rejectFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable Long requestId) {

        String receiverUsername = userDetails.getUsername();
        FriendRequestEntity request = friendshipRequestRepository.findById(requestId).orElse(null);

        if (request == null || request.getStatus() != RequestStatusEnum.PENDING) {
            throw new IllegalArgumentException("Invalid request");
        }

        UserEntity receiver = userRepository.findByUsername(receiverUsername).orElseThrow(()->{
            throw new UsernameNotFoundException("User not found");
        });

        if (!request.getReceiver().equals(receiver)) {
            throw new IllegalArgumentException("Request/receiver not cam equal");
        }

        request.setStatus(RequestStatusEnum.REJECTED);
        friendshipRequestRepository.save(request);
    }

    @Override
    @Transactional
    public ResponseEntity cancelFriendRequest(UserEntity sender, UserEntity receiver) {
        FriendRequestEntity request = friendshipRequestRepository.findBySenderAndReceiver(sender, receiver).orElseThrow();

        if (request == null || request.getStatus() != RequestStatusEnum.PENDING) {
            throw new IllegalArgumentException("Invalid friend request");
        }

        friendshipRequestRepository.delete(request);
        sender.getSubscribers().remove(receiver);

        userRepository.save(sender);

        return ResponseEntity.ok("Friendship request accept.");
    }
    public void removeFriend(UserEntity currentUser, UserEntity friendToRemove) {
        currentUser.getFriends().remove(friendToRemove);
        friendToRemove.getFriends().remove(currentUser);
        currentUser.getSubscribers().remove(friendToRemove);

        userRepository.save(currentUser);
        userRepository.save(friendToRemove);
    }
}