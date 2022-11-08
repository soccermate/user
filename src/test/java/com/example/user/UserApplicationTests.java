package com.example.user;

import com.example.user.repository.UserRepository;
import com.example.user.repository.entity.AppUser;
import com.example.user.utils.pictureUtils.PictureUtil;
import com.example.user.utils.communicationUtils.AuthenticationClient;
import com.example.user.service.messagingService.dto.UserCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;

@SpringBootTest
@Slf4j
@EnableConfigurationProperties
@ActiveProfiles(profiles = "dev")
class UserApplicationTests {

	@Autowired
	KafkaSender<Integer, UserCreatedMessage> userCreatedMessageKafkaSender;

	@Test
	void contextLoads() {



	}

	@Test
	void testProducer(){
		Flux<SenderRecord<Integer, UserCreatedMessage, Integer>> fl =
				Flux.just(new UserCreatedMessage(Long.valueOf(1), "email", "d", "", "a"))
						.map(msg -> SenderRecord.create(
								"user_created", null, null, 1, msg, 1));
		userCreatedMessageKafkaSender
				.send(fl)
				.doOnNext(r -> log.info(r.toString()))
				.subscribe();
	}


	@Autowired
	AuthenticationClient authenticationClient;

	@Test
	void testAuthenticationClient() throws Exception
	{
		Mono<ResponseEntity> responseEntityMono = authenticationClient.changeRoleToUser(6);
		responseEntityMono.subscribe(responseEntity -> {
			log.info(responseEntity.getStatusCode().toString());
		});

		Thread.sleep(3000);
	}

	@Autowired
	UserRepository userRepository;

	@Test
	void testUserRepository(){

		Mono<AppUser> appUserMono = userRepository.save(AppUser.builder().user_id(Long.valueOf(1)).nickname("test3").isNew(true).point(Long.valueOf(0)).profileImgUrl("https").region("test").build());
		StepVerifier.create(appUserMono).expectComplete().verify();



		Flux<AppUser> appUserFlux = userRepository.findAll();

		StepVerifier.create(appUserFlux).expectNextCount(Long.valueOf(1)).verifyComplete();

	}

	@Test
	void testUserRepositoryProfileImg(){

		userRepository.updateProfileImagePath("https://test", Long.valueOf(1)).subscribe();
		userRepository.updateNickname("fromSpring", Long.valueOf(1)).subscribe();
		userRepository.updateRegion("서울시 성동구", Long.valueOf(1)).subscribe();

		Mono<AppUser> appUserMono = userRepository.findById(Long.valueOf(1));
		StepVerifier.create(appUserMono).expectNextMatches(
				AppUser ->{
					return AppUser.getUser_id().equals(Long.valueOf(1)) &&
							AppUser.getNickname().equals("fromSpring") &&
							AppUser.getRegion().equals("서울시 성동구") &&
							AppUser.getProfileImgUrl().equals("https://test");
				}
		).verifyComplete();

	}

	@Test
	void testUserRepositoryPatchUser(){

		userRepository.patchUser("patch", "서울시 강북구", "https://patch", Long.valueOf(1)).subscribe();

		Mono<AppUser> appUserMono = userRepository.findById(Long.valueOf(1));
		StepVerifier.create(appUserMono).expectNextMatches(
				AppUser ->{
					return AppUser.getUser_id().equals(Long.valueOf(1)) &&
							AppUser.getNickname().equals("patch") &&
							AppUser.getRegion().equals("서울시 강북구") &&
							AppUser.getProfileImgUrl().equals("https://patch");
				}
		).verifyComplete();

	}

	@Test
	void testUserRepositoryUserExistByNickname(){

		Mono<Boolean> result = userRepository.existsByNickname("patch1");

		StepVerifier.create(result).expectNextMatches(
				a -> {
					return a.equals(Boolean.FALSE);
				}
		).verifyComplete();
	}

	@Autowired
	PictureUtil pictureUtil;

	@Test
	void testS3Upload()
	{

	}


}
