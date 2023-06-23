package ru.vados.effectiveMobile.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Getter
@Setter
public class PostDto {
    private Long id;
    private String text;
    private String title;
    private UserDto user;

    @Data
    @Value
    public static class PostCreate{
        String text;
        String title;
    }

    @Data
    @Value
    public static class PostUpdate{
        Long id;
        String text;
        String title;
    }

}