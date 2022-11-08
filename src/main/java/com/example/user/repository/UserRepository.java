package com.example.user.repository;

import com.example.user.repository.entity.AppUser;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<AppUser,Long>
{
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET profile_img_url = :path WHERE user_id = :id")
    public Mono<Void> updateProfileImagePath(@Param("path") String profile_img_path, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET region = :region WHERE user_id = :id")
    public Mono<Void> updateRegion(@Param("region") String region, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET nickname = :nickname WHERE user_id = :id")
    public Mono<Void> updateNickname(@Param("nickname") String nickname, @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET nickname = :nickname, region = :region, profile_img_url = :path WHERE user_id = :id")
    public Mono<Void> patchUser(@Param("nickname") String nickname, @Param("region") String region, @Param("path") String profile_img_path,  @Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET point = point + :point where user_id = :userId")
    public Mono<Integer> incrementPoint(@Param("point") int point, @Param("userId") Long userId);


    public Mono<Boolean> existsByNickname(String nickname);

}
