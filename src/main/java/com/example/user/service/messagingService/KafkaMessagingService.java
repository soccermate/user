package com.example.user.service.messagingService;

import com.example.user.repository.UserRepository;
import com.example.user.repository.entity.AppUser;
import com.example.user.service.messagingService.dto.PointEarnedMessage;
import com.example.user.service.messagingService.dto.UserCreatedMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

@Service
@Slf4j
public class KafkaMessagingService implements  MessageService
{
    private final Flux<ReceiverRecord<Integer, String>> inboundUserCreatedMsg;

    private final Flux<ReceiverRecord<Integer, String>> inboundPointEarnedMsg;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    public KafkaMessagingService(
            @Qualifier("getUserCreatedReceiverOption") ReceiverOptions<Integer, String> userCreatedReceiverOptions
            , @Qualifier("getPointEarnedReceiverOption") ReceiverOptions<Integer, String> pointEarnedReceiverOptions
            , UserRepository userRepository)
    {
        this.userRepository = userRepository;

        inboundUserCreatedMsg = KafkaReceiver.create(userCreatedReceiverOptions).receive();
        inboundPointEarnedMsg = KafkaReceiver.create(pointEarnedReceiverOptions).receive();

        objectMapper = new ObjectMapper();

        inboundUserCreatedMsg.doOnError(e ->{
            log.error(e.toString());
        })
                .flatMap(r -> {
                    UserCreatedMessage userCreatedMessage = null;
                    try {
                            userCreatedMessage = objectMapper.readValue(r.value(), UserCreatedMessage.class);
                            log.debug(userCreatedMessage.toString());
                            AppUser newUser = AppUser.builder()
                                .user_id(userCreatedMessage.getId())
                                .isNew(true)
                                .point(Long.valueOf(0)).build();

                            r.receiverOffset().acknowledge();

                            return userRepository.save(newUser);

                        } catch (JsonProcessingException e)
                        {
                            r.receiverOffset().acknowledge();
                            log.error(e.toString());
                            return Mono.empty();
                        }
                    }
                )
                .filter(appUser -> {return appUser == null;})
                .subscribe();

        inboundPointEarnedMsg.doOnError(e ->{
                    log.error(e.toString());
                })
                .flatMap(r -> {
                            PointEarnedMessage pointEarnedMessage = null;
                            try {
                                pointEarnedMessage = objectMapper.readValue(r.value(), PointEarnedMessage.class);

                                log.debug(pointEarnedMessage.toString());

                                int pointEarned = pointEarnedMessage.getEarnedPoint();
                                Long userId = pointEarnedMessage.getUserId();

                                r.receiverOffset().acknowledge();

                                return userRepository.incrementPoint(pointEarned, userId);

                            } catch (JsonProcessingException e)
                            {
                                r.receiverOffset().acknowledge();
                                log.error(e.toString());
                                return Mono.empty();
                            }
                        }
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

    }


}
