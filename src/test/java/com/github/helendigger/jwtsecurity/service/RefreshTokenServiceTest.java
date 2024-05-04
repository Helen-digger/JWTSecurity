package com.github.helendigger.jwtsecurity.service;

import com.github.helendigger.jwtsecurity.configuration.JwtConfiguration;
import com.github.helendigger.jwtsecurity.configuration.PostgresConfiguration;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = {PostgresConfiguration.class})
public class RefreshTokenServiceTest implements JwtConfiguration {
    @Autowired
    RefreshTokenService service;
    @Test
    public void saveTest() {
        var saved = service.save(1L);
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(1L, saved.getUserId());
        Assertions.assertNotNull(saved.getUserId());
        Assertions.assertNotNull(saved.getValue());
    }

    @Test
    public void expiryTest() {
        var saved = service.save(2L);
        Assertions.assertNotNull(saved);
        Awaitility.await().pollDelay(6L, TimeUnit.SECONDS)
                .until(() -> service.getByValue(saved.getValue()), Optional::isEmpty);
        var opt = service.getByValue(saved.getValue());
        Assertions.assertTrue(opt.isEmpty());
    }

    @Test
    public void expiryByAccessTest() {
        var saved = service.save(3L);
        Assertions.assertNotNull(saved);
        var opt = service.getByValue(saved.getValue());
        Assertions.assertTrue(opt.isPresent());
        opt = service.getByValue(saved.getValue());
        Assertions.assertTrue(opt.isEmpty());
    }

    @Test
    public void getTest() {
        var saved = service.save(2L);
        Assertions.assertNotNull(saved);
        var opt = service.getByValue(saved.getValue());
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals(saved, opt.get());
    }
}
