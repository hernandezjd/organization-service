package io.orkidea.organizations.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class GitPropertiesProvider {

    private final String commitHash;

    public GitPropertiesProvider() {
        this.commitHash = loadCommitHash();
    }

    public String getCommitHash() {
        return commitHash;
    }

    private String loadCommitHash() {
        try {
            ClassPathResource resource = new ClassPathResource("git.properties");
            if (resource.exists()) {
                Properties props = new Properties();
                props.load(resource.getInputStream());
                return props.getProperty("git.commit.id.abbrev", "unknown");
            }
        } catch (IOException e) {
            // git.properties not found, use default
        }
        return "unknown";
    }
}
