package com.example.idcard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class IdCardApplicationTest {

    @Test
    void contextLoads() {
        // Verify the application context loads successfully with SQLite
    }
}