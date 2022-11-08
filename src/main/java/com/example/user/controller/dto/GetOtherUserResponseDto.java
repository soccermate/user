package com.example.user.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class GetOtherUserResponseDto {
    private final String region;

    private final String profile_picture_path;

    private final String nickname;
}
