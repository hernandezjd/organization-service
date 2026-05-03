package io.orkidea.organizations.rest;

import io.orkidea.organizations.api.OrganizationsApi;
import io.orkidea.organizations.dto.Address;
import io.orkidea.organizations.dto.CreateOrganizationRequest;
import io.orkidea.organizations.dto.OrganizationResponse;
import io.orkidea.organizations.dto.UpdateOrganizationRequest;
import io.orkidea.organizations.model.OrganizationEntity;
import io.orkidea.organizations.service.OrganizationCommandService;
import io.orkidea.organizations.service.OrganizationQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class OrganizationsController implements OrganizationsApi {

    private final OrganizationCommandService commandService;
    private final OrganizationQueryService queryService;

    public OrganizationsController(
            OrganizationCommandService commandService,
            OrganizationQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @Override
    public ResponseEntity<OrganizationResponse> createOrganization(CreateOrganizationRequest request) {
        UUID organizationId = commandService.createOrganization(request);
        OrganizationResponse response = new OrganizationResponse(
            organizationId.toString(),
            request.name(),
            request.contactEmail(),
            request.address(),
            "ACTIVE",
            null,
            null
        );

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(organizationId)
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteOrganization(UUID id) {
        commandService.deactivateOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OrganizationResponse> getOrganizationById(UUID id) {
        OrganizationEntity entity = queryService.getOrganizationById(id);
        OrganizationResponse response = mapToResponse(entity);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<OrganizationResponse>> listOrganizations() {
        List<OrganizationEntity> entities = queryService.getAllOrganizations();
        List<OrganizationResponse> responses = entities.stream()
            .map(this::mapToResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<OrganizationResponse> updateOrganization(UUID id, UpdateOrganizationRequest request) {
        commandService.updateOrganization(id, request);
        OrganizationEntity entity = queryService.getOrganizationById(id);
        OrganizationResponse response = mapToResponse(entity);
        return ResponseEntity.ok(response);
    }

    private OrganizationResponse mapToResponse(OrganizationEntity entity) {
        Address address = entity.getStreet() != null || entity.getCity() != null || entity.getState() != null
            || entity.getPostalCode() != null || entity.getCountry() != null
            ? new Address(
                entity.getStreet(),
                entity.getCity(),
                entity.getState(),
                entity.getPostalCode(),
                entity.getCountry()
            )
            : null;

        return new OrganizationResponse(
            entity.getOrganizationId(),
            entity.getName(),
            entity.getContactEmail(),
            address,
            entity.getStatus().toString(),
            entity.getCreatedAt(),
            entity.getModifiedAt()
        );
    }
}
