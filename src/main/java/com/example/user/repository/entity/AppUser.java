package com.example.user.repository.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name="users")
public class AppUser implements Persistable<Long> {

    @Id
    private Long user_id;

    private String nickname;

    @Column("profile_img_url")
    private String profileImgUrl;

    private String region;

    private Long point;

    @Transient
    private boolean isNew;

    @Override
    public Long getId() {
        return user_id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}
