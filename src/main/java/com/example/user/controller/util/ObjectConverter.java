package com.example.user.controller.util;

import com.example.user.controller.dto.VerifyTokenResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class ObjectConverter
{
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static VerifyTokenResultDto convertAuthCredentials(String authCredentialsJsonStr)
    {
        return objectMapper.readValue(authCredentialsJsonStr, VerifyTokenResultDto.class);
    }
}
