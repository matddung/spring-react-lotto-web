package com.studyjun.lottoweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.studyjun.lottoweb.config.Provider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private Provider provider;

    private String providerId;

    private String role;

    @Builder
    public User(String email, String password, String role, Provider provider, String providerId, String nickname) {
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.providerId = providerId;
        this.nickname = nickname;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}