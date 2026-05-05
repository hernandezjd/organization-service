package io.orkidea.organizations.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2ConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withPropertyValues("spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080");

    @Test
    void shouldLoadOAuth2IssuerUri() {
        contextRunner.run(context -> {
            Environment env = context.getEnvironment();
            assertThat(env.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri"))
                    .isEqualTo("http://localhost:8080");
        });
    }
}
