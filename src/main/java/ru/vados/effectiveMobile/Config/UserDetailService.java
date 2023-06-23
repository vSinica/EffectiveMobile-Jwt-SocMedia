package ru.vados.effectiveMobile.Config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vados.effectiveMobile.Dto.UserDto;
import ru.vados.effectiveMobile.Entity.UserEntity;
import ru.vados.effectiveMobile.Entity.RoleEnum;
import ru.vados.effectiveMobile.Repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
@Primary
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void registerUser(String username, String email, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setEmail(email);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setRole(RoleEnum.USER);
        userRepository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(()->{
            throw new UsernameNotFoundException("User not found");
        });
        return new UserPrincipal(new UserDto.UserInfo(userEntity.getUsername(),userEntity.getEmail(),
                userEntity.getPassword(),userEntity.getRole().toString()));
    }

    public String login(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }
    public Optional<UserEntity> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

}


