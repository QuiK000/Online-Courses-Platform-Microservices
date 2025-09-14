package com.dev.quikkkk.auth_service;

import com.dev.quikkkk.auth_service.service.impl.BruteForceProtectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BruteForceProtectionServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private BruteForceProtectionServiceImpl bruteForceProtectionService;

    @BeforeEach
    void setUp() {
        // Убираем общую заглушку, будем настраивать для каждого теста отдельно
    }

    @Test
    void testRegisterFailedAttempt() {
        String ip = "192.168.1.1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey("blocked_ip_192.168.1.1")).thenReturn(false);
        when(valueOperations.increment("login_attempt_192.168.1.1")).thenReturn(1L);

        bruteForceProtectionService.registerFailedAttempt(ip);

        verify(valueOperations).increment("login_attempt_192.168.1.1");
        verify(redisTemplate).expire(eq("login_attempt_192.168.1.1"), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testBlockAfterMaxAttempts() {
        String ip = "192.168.1.1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey("blocked_ip_192.168.1.1")).thenReturn(false);
        when(valueOperations.increment("login_attempt_192.168.1.1")).thenReturn(5L);

        bruteForceProtectionService.registerFailedAttempt(ip);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture());

        assertEquals("blocked_ip_192.168.1.1", keyCaptor.getValue());
        assertNotNull(valueCaptor.getValue());

        verify(redisTemplate).expire(eq("blocked_ip_192.168.1.1"), eq(15L), eq(TimeUnit.MINUTES));
        verify(redisTemplate).delete("login_attempt_192.168.1.1");
    }

    @Test
    void testIsBlocked() {
        String ip = "192.168.1.1";

        when(redisTemplate.hasKey("blocked_ip_192.168.1.1")).thenReturn(true);

        assertTrue(bruteForceProtectionService.isBlocked(ip));
    }

    @Test
    void testIsNotBlocked() {
        String ip = "192.168.1.1";

        when(redisTemplate.hasKey("blocked_ip_192.168.1.1")).thenReturn(false);

        assertFalse(bruteForceProtectionService.isBlocked(ip));
    }

    @Test
    void testRegisterSuccessfulAttempt() {
        String ip = "192.168.1.1";

        bruteForceProtectionService.registerSuccessfulAttempt(ip);

        verify(redisTemplate).delete("login_attempt_192.168.1.1");
    }

    @Test
    void testGetRemainingAttemptsWhenNoAttempts() {
        String ip = "192.168.1.1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login_attempt_192.168.1.1")).thenReturn(null);

        int remaining = bruteForceProtectionService.getRemainingAttempts(ip);

        assertEquals(5, remaining);
    }

    @Test
    void testGetRemainingAttemptsWithSomeAttempts() {
        String ip = "192.168.1.1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login_attempt_192.168.1.1")).thenReturn(2L);

        int remaining = bruteForceProtectionService.getRemainingAttempts(ip);

        assertEquals(3, remaining);
    }

    @Test
    void testAlreadyBlockedIp() {
        String ip = "192.168.1.1";

        when(redisTemplate.hasKey("blocked_ip_192.168.1.1")).thenReturn(true);

        bruteForceProtectionService.registerFailedAttempt(ip);

        verify(redisTemplate, never()).opsForValue();
        verify(valueOperations, never()).increment(anyString());
    }

    @Test
    void testCleanupExpiredBlocks() {
        assertDoesNotThrow(() -> bruteForceProtectionService.cleanupExpiredBlocks());
    }

    @Test
    void testGetRemainingAttemptsWithMaxAttempts() {
        String ip = "192.168.1.1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("login_attempt_192.168.1.1")).thenReturn(5L);

        int remaining = bruteForceProtectionService.getRemainingAttempts(ip);

        assertEquals(0, remaining);
    }
}