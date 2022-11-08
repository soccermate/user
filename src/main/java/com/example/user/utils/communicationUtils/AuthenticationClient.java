package com.example.user.utils.communicationUtils;

import com.example.user.utils.communicationUtils.config.AuthenticationClientConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="${spring.reactive-feign.service-name.authentication}",
        url="${spring.reactive-feign.service-name.authentication.url}",
        configuration = AuthenticationClientConfig.class
)
public interface AuthenticationClient {

    @PutMapping("/user/{id}/role")
    public Mono<ResponseEntity> changeRoleToUser(@PathVariable long id);

}
