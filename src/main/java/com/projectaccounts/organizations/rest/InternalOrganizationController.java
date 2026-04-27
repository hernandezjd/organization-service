package com.projectaccounts.organizations.rest;

import com.projectaccounts.organizations.dto.CreateOrganizationRequest;
import com.projectaccounts.organizations.dto.OrganizationResponse;
import com.projectaccounts.organizations.service.OrganizationCommandService;
import com.projectaccounts.organizations.service.OrganizationQueryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class InternalOrganizationController {

    private final OrganizationCommandService commandService;
    private final OrganizationQueryService queryService;

    public InternalOrganizationController(OrganizationCommandService commandService,
                                          OrganizationQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/organizations")
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
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
            .replacePath("/organizations/{id}")
            .buildAndExpand(organizationId)
            .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
