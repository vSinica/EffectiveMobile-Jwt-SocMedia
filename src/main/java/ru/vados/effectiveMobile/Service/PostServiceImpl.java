package ru.vados.effectiveMobile.Service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.vados.effectiveMobile.Dto.PostDto;
import ru.vados.effectiveMobile.Dto.UserDto;
import ru.vados.effectiveMobile.Entity.PostEntity;
import ru.vados.effectiveMobile.Entity.UserEntity;
import ru.vados.effectiveMobile.Repository.PostRepository;
import ru.vados.effectiveMobile.Repository.UserRepository;

import javax.persistence.Access;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public void createPost(PostDto.PostCreate post, String userName) {
        PostEntity entity = new PostEntity();
        entity.setText(post.getText());
        entity.setTitle(post.getTitle());
        entity.setUser(userRepository.findByUsername(userName).orElseThrow());

        postRepository.save(entity);
    }

    public PostDto getPostById(Long postId) {
        Optional<PostEntity> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            PostEntity post = optionalPost.get();

            PostDto postDto = new PostDto();
            postDto.setId(post.getId());
            postDto.setText(post.getText());
            postDto.setTitle(post.getTitle());

            return postDto;
        }

        return null;
    }

    @Override
    public List<PostDto> getAllPostsByUserId(Long userId) {
        List<PostEntity> posts = postRepository.findAllByUserId(userId);

        List<PostDto> postDtos = new ArrayList<>();

        for (PostEntity post : posts) {
            PostDto postDTO = new PostDto();
            postDTO.setId(post.getId());
            postDTO.setText(post.getText());
            postDTO.setTitle(post.getTitle());

            postDtos.add(postDTO);
        }
        return postDtos;
    }

    @Override
    public boolean updatePost(PostDto.PostUpdate updatedPost) {
        Optional<PostEntity> optionalPost = postRepository.findById(updatedPost.getId());
        if (optionalPost.isPresent()) {
            PostEntity postEntity = optionalPost.get();
            postEntity.setTitle(updatedPost.getTitle());
            postEntity.setText(updatedPost.getText());
            postRepository.save(postEntity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deletePost(Long postId) {
        Optional<PostEntity> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            postRepository.deleteById(postId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isPostBelongsToUser(Long postId, String username) {
        PostEntity postEntity = postRepository.findById(postId).orElse(null);
        if (postEntity == null) {
            return false; // Пост не найден
        }

        UserEntity postUser = postEntity.getUser();
        return postUser.getUsername().equals(username);
    }

    @Override
    public List<PostEntity> getPostsByUsers(Set<UserEntity> users, int page, int pageSize) {
        Set<Long> userIds = users.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        return postRepository.findByUserIdIn(userIds, pageable);
    }

    private PostDto convertToPostDto(PostEntity postEntity) {
        PostDto postDto = new PostDto();
        postDto.setId(postEntity.getId());
        postDto.setText(postEntity.getText());
        postDto.setTitle(postEntity.getTitle());

        UserDto userDto = new UserDto();
        userDto.setId(postEntity.getUser().getId());
        userDto.setUsername(postEntity.getUser().getUsername());
        userDto.setEmail(postEntity.getUser().getEmail());

        postDto.setUser(userDto);

        return postDto;
    }

    @Override
    public List<PostDto> getActivityFeed(Long userId, int page, int pageSize) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<UserEntity> subscriptions = userEntity.getSubscribers();

        List<PostEntity> posts = getPostsByUsers(subscriptions, page, pageSize);

        return posts.stream()
                .map(this::convertToPostDto)
                .collect(Collectors.toList());
    }
}
