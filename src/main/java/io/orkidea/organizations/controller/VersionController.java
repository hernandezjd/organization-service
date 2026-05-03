package io.orkidea.organizations.controller;

import io.orkidea.organizations.util.GitPropertiesProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

    private final String serviceName;
    private final String version;
    private final String commitHash;

    public VersionController(
            @Value("${project.name:organization-service}") String serviceName,
            @Value("${project.version:unknown}") String version,
            GitPropertiesProvider gitPropertiesProvider) {
        this.serviceName = serviceName;
        this.version = version;
        this.commitHash = gitPropertiesProvider.getCommitHash();
    }

    @GetMapping
    public VersionInfo getVersion() {
        return new VersionInfo(serviceName, version, commitHash);
    }

    public record VersionInfo(String serviceName, String version, String commitHash) {}
}
