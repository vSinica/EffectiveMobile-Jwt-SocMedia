package ru.vados.effectiveMobile.Service;

import ru.vados.effectiveMobile.Dto.PostDto;
import ru.vados.effectiveMobile.Entity.PostEntity;
import ru.vados.effectiveMobile.Entity.UserEntity;

import java.util.List;
import java.util.Set;

public interface PostService {
    void createPost(PostDto.PostCreate post, String userName);
    PostDto getPostById(Long postId);
    List<PostDto> getAllPostsByUserId(Long userId);
    boolean updatePost(PostDto.PostUpdate updatedPost);
    boolean deletePost(Long postId);
    boolean isPostBelongsToUser(Long postId, String username);
    List<PostEntity> getPostsByUsers(Set<UserEntity> users, int page, int pageSize);
    List<PostDto> getActivityFeed(Long userId, int page, int pageSize);
}
