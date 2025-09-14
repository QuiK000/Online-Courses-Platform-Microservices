package com.dev.quikkkk.auth_service;

import com.dev.quikkkk.auth_service.utils.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NetworkUtilsTest {
    @Mock
    private HttpServletRequest request;

    @Test
    void testGetClientIpFromXForwardedFor() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1", "10.0.0.1");
        String result = NetworkUtils.getClientIp(request);
        assertEquals("192.168.1.1", result);
    }

    @Test
    void testGetClientIpFromRemoteAddr() {
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        String result = NetworkUtils.getClientIp(request);
        assertEquals("192.168.1.100", result);
    }

    @Test
    void testGetClientIpFromRequestContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.50");

        assertTrue(NetworkUtils.getClientIp().isEmpty());
    }
}
