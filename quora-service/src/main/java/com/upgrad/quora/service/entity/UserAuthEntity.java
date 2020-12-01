package com.upgrad.quora.service.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_auth")
@NamedQueries({
        @NamedQuery(name = "getUserAuthByAccessToken",query = "Select u from UserAuthEntity u where u.accessToken=:accessToken"),
})
public class UserAuthEntity {

    //id column is primary key
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //uuid column is universal unique identity field
    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    //user_id column will be mapped to User object
    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private UserEntity user;

    //access_token column will map to JWT generated token
    @Column(name = "access_token")
    @Size(max = 500)
    private String accessToken;

    //expires_at column will be mapped to the expiration time of access_token
    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    //login_at column will be mapped to the last login time of user.
    @Column(name = "login_at")
    private ZonedDateTime loginAt;

    //logout_at column will be mapped to last logout time of user.
    @Column(name = "logout_at")
    private ZonedDateTime logoutAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }
}
