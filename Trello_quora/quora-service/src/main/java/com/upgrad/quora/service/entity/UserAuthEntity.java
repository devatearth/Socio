package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Table(name = "user_auth")
public class UserAuthEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "access_token")
    @Size(max = 500)
    private String accessToken;

    @Column(name = "expires_at")
    private Timestamp expiresAt;

    @Column(name = "login_at")
    private Timestamp loginAt;

    @Column(name = "logout_at")
    private Timestamp logoutAt;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Timestamp getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(Timestamp loginAt) {
        this.loginAt = loginAt;
    }

    public Timestamp getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(Timestamp logoutAt) {
        this.logoutAt = logoutAt;
    }
}
