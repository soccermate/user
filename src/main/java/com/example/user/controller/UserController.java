package com.example.user.controller;

import com.example.user.controller.dto.*;
import com.example.user.controller.exceptions.UserNotFoundException;
import com.example.user.controller.util.ObjectConverter;
import com.example.user.repository.entity.AppUser;
import com.example.user.service.UserService;
import com.example.user.utils.pictureUtils.PictureUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@Slf4j
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private static final String AUTH_CREDENTIALS = "auth_credentials";
    private final PictureUtil pictureUtil;

    private final UserService userService;


    @GetMapping
    public Mono<ResponseEntity<GetUserResponseDto>> getUser(@RequestHeader(AUTH_CREDENTIALS) String authStr)
    {

        VerifyTokenResultDto verifyTokenResultDto = ObjectConverter.convertAuthCredentials(authStr);

        Mono<AppUser> appUserMono = userService.getUser(verifyTokenResultDto.getUser_id());

        return appUserMono.map(appUser -> {
            return ResponseEntity.ok(GetUserResponseDto.builder()
                    .nickname(appUser.getNickname())
                    .point(appUser.getPoint())
                    .region(appUser.getRegion())
                    .profile_picture_path(appUser.getProfileImgUrl())
                    .build());
        });
    }

    @GetMapping(value = "{id}")
    public Mono<ResponseEntity<GetOtherUserResponseDto>> getOtherUser(@RequestHeader(AUTH_CREDENTIALS) String authStr,
                                                                      @PathVariable long id)
    {
        Mono<AppUser> appUserMono = userService.getUser(id);


        return appUserMono
                .switchIfEmpty(Mono.defer(() ->{
                    throw new UserNotFoundException("user with the id " + String.valueOf(id) + " not found!");
                }))
                .map(appUser -> {

                    log.info(appUser.toString());
                    return ResponseEntity.ok(GetOtherUserResponseDto.builder()
                        .nickname(appUser.getNickname())
                        .region(appUser.getRegion())
                        .profile_picture_path(appUser.getProfileImgUrl())
                        .build());
        });
    }

    @PutMapping("/nickname")
    public Mono<Void> userNicknameUpdate(@RequestHeader(AUTH_CREDENTIALS) String authStr
            , @Valid @RequestBody UserNicknameUpdateRequestDto userNicknameUpdateRequestDto)
    {
        VerifyTokenResultDto verifyTokenResultDto = ObjectConverter.convertAuthCredentials(authStr);

        return userService
                .updateNickname(userNicknameUpdateRequestDto.getNickname(),
                        verifyTokenResultDto.getUser_id());
    }

    @PutMapping("/region")
    public Mono<Void> userRegionUpdate(@RequestHeader(AUTH_CREDENTIALS) String authStr
            , @Valid @RequestBody UserRegionUpdateRequestDto userRegionUpdateRequestDto)
    {
        VerifyTokenResultDto verifyTokenResultDto = ObjectConverter.convertAuthCredentials(authStr);

        return userService
                .updateRegion(userRegionUpdateRequestDto.getRegion(),
                        verifyTokenResultDto.getUser_id());
    }

    @PutMapping(value = "/profile_picture",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } )
    public Mono<Void> userProfilePictureUpdate(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @NotBlank(message = "picture should not be blank") @RequestPart("picture") FilePart picture){

        VerifyTokenResultDto verifyTokenResultDto = ObjectConverter.convertAuthCredentials(authStr);

        userService
                .getUser(verifyTokenResultDto.getUser_id())
                .flatMap(appUser -> {
                    String path = appUser.getProfileImgUrl();
                    if( path == null)
                    {
                        return Mono.empty();
                    }
                    else{
                        return pictureUtil.deletePicture(path);
                    }
                })
                .flatMap(deleteResponse -> {
                    return pictureUtil
                            .uploadUserProfilePict(picture, verifyTokenResultDto.getUser_id());
                })
                .flatMap(path -> userService.updateProfileImagePath(path, verifyTokenResultDto.getUser_id()))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();


        return Mono.empty();

    }

    @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } )
    public Mono<Void> userPatch(
            @RequestHeader(AUTH_CREDENTIALS) String authStr,
            @NotBlank(message="picture should not be blank") @RequestPart("picture") FilePart picture,
            @NotBlank(message="region should not be blank") @RequestPart("region") String region,

            @NotBlank(message="nickname should not be blank")
            @Size(min = 3, max = 15, message = "nickname should be between 3 and 15 characters long")
            @RequestPart("nickname") String nickname){

        log.info("userPatch called!");


        VerifyTokenResultDto verifyTokenResultDto = ObjectConverter.convertAuthCredentials(authStr);

        return userService
                .getUser(verifyTokenResultDto.getUser_id())
                .flatMap(appUser -> {
                    log.info("started to delete file!");
                    String path = appUser.getProfileImgUrl();
                    if( path == null)
                    {
                        log.info("path is null!!");
                        return Mono.just(true);
                    }
                    else{
                        log.info(pictureUtil.deletePicture(path).toString());
                        return Mono.just(true);
                    }
                })
                .flatMap(deleteResult -> {
                    return userService.patchUser(nickname, region, picture, verifyTokenResultDto.getUser_id());
                });

    }

    @PostMapping(value ="nickname/checkduplicate")
    public Mono<ResponseEntity<UserNicknameExistByResponseDto>> userNicknameExistBy(
            @Valid @RequestBody UserNicknameExistByRequestDto userNicknameExistByRequestDto)
    {
        return userService.existsByNickname(userNicknameExistByRequestDto.getNickname())
                .map(boolResult -> {
                    return ResponseEntity.ok(new UserNicknameExistByResponseDto(boolResult));
                });
    }








}
