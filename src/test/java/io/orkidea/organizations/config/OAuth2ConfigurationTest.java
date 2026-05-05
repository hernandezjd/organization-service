package io.orkidea.organizations.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OAuth2ConfigurationTest {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Test
    void shouldLoadOAuth2IssuerUri() {
        assertNotNull(issuerUri);
    }

    @Test
    void shouldUseDefaultIssuerUri() {
        assertEquals("http://localhost:8080", issuerUri);
    }
}
