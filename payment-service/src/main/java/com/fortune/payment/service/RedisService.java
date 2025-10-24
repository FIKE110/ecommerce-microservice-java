package com.fortune.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, String >redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void addKey(String key,String value) {
        if(key!=null && value!=null) {
            save(key,value);
        }
    }


    public void save(String key,String value){
        redisTemplate.opsForValue().set(key+"@KEY",value, Duration.ofHours(23));
    }


    public String get(String key){
        return redisTemplate.opsForValue().get(key+"@KEY");
    }

    public String getAndDelete(String key){
        return redisTemplate.opsForValue().getAndDelete(key+"@KEY");
    }
}

