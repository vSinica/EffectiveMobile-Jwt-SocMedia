package ru.vados.effectiveMobile.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vados.effectiveMobile.Entity.PostEntity;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findAllByUserId(Long userId);
    List<PostEntity> findByUserIdIn(Collection<Long> userIds, Pageable pageable);
}
