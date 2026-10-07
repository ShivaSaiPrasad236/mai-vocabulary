package com.my_projects.mai_vocabulary.service;

import com.my_projects.mai_vocabulary.dto.VocabularyResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class VocabularyCacheService {

    private static final Duration CACHE_DURATION = Duration.ofHours(1);

    private final RedisTemplate<String, Object> redisTemplate;

    public VocabularyCacheService(RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public VocabularyResponse get(Long userId, String word) {

        String key = buildKey(userId, word);
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof VocabularyResponse) {
            return (VocabularyResponse) value;
        }
        return null;
    }

    public void put(Long userId, String word, VocabularyResponse entry) {

        String key = buildKey(userId, word);
        redisTemplate.opsForValue().set(key, entry, CACHE_DURATION);
    }

    public void evict(Long userId, String word) {

        String key = buildKey(userId, word);
        redisTemplate.delete(key);
    }

    private String buildKey(Long userId, String word) {

        return "vocabulary:user:"
                + userId
                + ":word:"
                + word;
    }
}
