package com.example.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserNicknameUpdateRequestDto {


    @NotBlank(message = "nickname should not be blank")
    @Size(min = 3, max = 15, message = "nickname should be between 3 and 15 characters long")
    private final String nickname;
}
