package ru.vados.effectiveMobile.Dto;

import lombok.*;

@Getter
@Setter
@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;

    @Data
    @Value
    public static class UserInfo {
        String username;
        String email;
        String password;
        String authority;
    }

    @Data
    @Value
    public static class UserRegister {
        String username;
        String email;
        String password;
        String authority;
    }

    @Data
    @Value
    @AllArgsConstructor
    public static class UserLogin {
        String username;
        String password;
    }
}
