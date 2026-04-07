package com.projectaccounts.organizations.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupListener.class);

    private final String serviceName;
    private final String version;
    private final String commitHash;

    public ApplicationStartupListener(
            @Value("${project.name:organization-service}") String serviceName,
            @Value("${project.version:unknown}") String version,
            @Value("${git.commit.id.abbrev:unknown}") String commitHash) {
        this.serviceName = serviceName;
        this.version = version;
        this.commitHash = commitHash;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Service started: {} v{} (commit: {})", serviceName, version, commitHash);
    }
}
