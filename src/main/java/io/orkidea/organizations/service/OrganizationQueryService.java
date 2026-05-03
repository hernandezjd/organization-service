package io.orkidea.organizations.service;

import io.orkidea.organizations.model.OrganizationEntity;
import io.orkidea.organizations.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationQueryService {

    private final OrganizationRepository organizationRepository;

    public OrganizationQueryService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public OrganizationEntity getOrganizationById(UUID id) {
        return organizationRepository.findById(id.toString())
            .orElseThrow(() -> new OrganizationNotFoundException(id));
    }

    public List<OrganizationEntity> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    public static class OrganizationNotFoundException extends RuntimeException {
        public OrganizationNotFoundException(UUID id) {
            super("Organization not found: " + id);
        }
    }
}
