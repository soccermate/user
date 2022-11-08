package com.example.user.filters;

import com.example.user.controller.dto.VerifyTokenResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Slf4j
public class CredentialInterceptor implements WebFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final String AUTH_CREDENTIALS = "auth_credentials";

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String credentials = exchange.getRequest().getHeaders().get(AUTH_CREDENTIALS).get(0);

        VerifyTokenResultDto verifyTokenResultDto = objectMapper.readValue(credentials, VerifyTokenResultDto.class);

        log.debug("credentials: " + credentials);
        return chain.filter(exchange).contextWrite(
                context -> {
                    context.put(AUTH_CREDENTIALS, verifyTokenResultDto);
                    return context;
                }
        );
    }
}
