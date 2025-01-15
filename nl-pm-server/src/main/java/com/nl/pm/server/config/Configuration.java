package com.nl.pm.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {
    @Value("${jwt.expireDateTime}")
    private Long JWT_EXPIRE_TIME;

    public Long getJWT_EXPIRE_TIME() {
        return JWT_EXPIRE_TIME;
    }

    public void setJWT_EXPIRE_TIME(Long JWT_EXPIRE_TIME) {
        this.JWT_EXPIRE_TIME = JWT_EXPIRE_TIME;
    }
}
