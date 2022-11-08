package com.example.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserRegionUpdateRequestDto {

    @NotBlank(message = "message should not be blank!")
    private final String region;
}
