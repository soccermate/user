package com.example.user.service;

import com.example.user.repository.UserRepository;
import com.example.user.repository.entity.AppUser;
import com.example.user.utils.communicationUtils.AuthenticationClient;
import com.example.user.utils.pictureUtils.PictureUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationClient authenticationClient;

    private final PictureUtil pictureUtil;

    public Mono<Void> updateProfileImagePath( String profile_img_path,  Long id){
        return userRepository.updateProfileImagePath(profile_img_path, id);
    }


    public Mono<Void> updateRegion( String region,  Long id){
        return userRepository.updateRegion(region, id);
    }


    public Mono<Void> updateNickname( String nickname,  Long id){
        return userRepository.updateNickname(nickname, id);
    }


    //this method takes too long but have to make sure that the user role is changed. So do it synchronously
    public Mono<Void> patchUser(String nickname, String region, FilePart picture,  Long id){
        return pictureUtil.uploadUserProfilePict(picture, id)
                .flatMap(picturePath -> {
                    //if next this return Mono<void>, flatMap after this never gets executed!
                    log.info("userRepository patchUser called!");
                    return userRepository
                            .patchUser(nickname, region, picturePath, id).thenReturn(id);
                })
                .flatMap( userId -> authenticationClient.changeRoleToUser(userId))
                .flatMap( r -> {
                    log.info(r.toString());
                    return Mono.empty();
                });
    }

    public Mono<AppUser> getUser(Long id)
    {
        return userRepository.findById(id);
    }

    public Mono<Boolean> existsByNickname(String nickname)
    {
        return userRepository.existsByNickname(nickname);
    }



}
