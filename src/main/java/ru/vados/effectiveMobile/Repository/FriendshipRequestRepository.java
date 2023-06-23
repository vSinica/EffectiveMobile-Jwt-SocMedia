package ru.vados.effectiveMobile.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vados.effectiveMobile.Entity.FriendRequestEntity;
import ru.vados.effectiveMobile.Entity.UserEntity;

import java.util.Optional;

@Repository
public interface FriendshipRequestRepository extends JpaRepository<FriendRequestEntity, Long> {
    Optional<FriendRequestEntity> findBySenderAndReceiver(UserEntity sender, UserEntity receiver);
}