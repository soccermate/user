package com.example.user.service.messagingService.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@ToString
@EqualsAndHashCode
public class UserCreatedMessage {

    private Long id;

    private final String email;

    private final String provider;

    private final String externalProviderName;

    private final String role;

}

