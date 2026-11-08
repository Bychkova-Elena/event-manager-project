package dev.sorokin.eventmanager.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class RedisLockManager {

    private final StringRedisTemplate redisTemplate;

    private static final String RELEASE_LOCK_LUA_SCRIPT = """
              if redis.call('get', KEYS[1]) == ARGV[1] then 
                return redis.call('del', KEYS[1])
              end return 0
            """;

    public String tryLock(
            String key,
            Duration ttl
    ) {
        String lockKey = "lock: " + key;
        String token = UUID.randomUUID().toString();

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                lockKey,
                token,
                ttl
        );

        if (!Boolean.TRUE.equals(locked)) {
            return null;
        }

        return token;
    }

    public void unlockLock(
            String key,
            String lockId
    ) {
        String lockKey = "lock: " + key;

        redisTemplate.execute(connection -> connection.scriptingCommands().eval(
                RELEASE_LOCK_LUA_SCRIPT.getBytes(StandardCharsets.UTF_8),
                ReturnType.INTEGER,
                1,
                lockKey.getBytes(StandardCharsets.UTF_8),
                lockId.getBytes(StandardCharsets.UTF_8)
        ), true);
    }
}
