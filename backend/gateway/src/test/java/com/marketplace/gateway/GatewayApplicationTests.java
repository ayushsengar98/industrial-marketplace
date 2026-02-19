package com.marketplace.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.gateway.enabled=false",
    "spring.main.web-application-type=reactive",
    "spring.security.enabled=false"
})
class GatewayApplicationTests {

    @Test
    void contextLoads() {
        // Test passes if context loads
    }
}