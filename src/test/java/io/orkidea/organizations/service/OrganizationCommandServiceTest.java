package io.orkidea.organizations.service;

import io.orkidea.organizations.command.CreateOrganizationCommand;
import io.orkidea.organizations.command.UpdateOrganizationCommand;
import io.orkidea.organizations.command.DeactivateOrganizationCommand;
import io.orkidea.organizations.dto.CreateOrganizationRequest;
import io.orkidea.organizations.dto.UpdateOrganizationRequest;
import io.orkidea.organizations.dto.Address;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationCommandServiceTest {

    @Mock
    private CommandGateway commandGateway;

    private OrganizationCommandService commandService;

    @BeforeEach
    void setUp() {
        commandService = new OrganizationCommandService(commandGateway);
    }

    @Test
    void shouldCreateOrganization_whenValidRequest() {
        // Given
        CreateOrganizationRequest request = new CreateOrganizationRequest(
            "Acme Corp",
            "contact@acme.com",
            new Address("123 Main St", "San Francisco", "CA", "94105", "USA")
        );

        // When
        UUID result = commandService.createOrganization(request);

        // Then
        assertNotNull(result);
        verify(commandGateway).sendAndWait(any(CreateOrganizationCommand.class));
    }

    @Test
    void shouldCreateOrganization_withoutAddress() {
        // Given
        CreateOrganizationRequest request = new CreateOrganizationRequest(
            "Another Corp",
            "hello@example.com",
            null
        );

        // When
        UUID result = commandService.createOrganization(request);

        // Then
        assertNotNull(result);
        verify(commandGateway).sendAndWait(any(CreateOrganizationCommand.class));
    }

    @Test
    void shouldUpdateOrganization_whenValidRequest() {
        // Given
        UUID orgId = UUID.randomUUID();
        UpdateOrganizationRequest request = new UpdateOrganizationRequest(
            "Updated Corp",
            "updated@example.com",
            new Address("456 Oak Ave", "Boston", "MA", "02101", "USA")
        );

        // When
        commandService.updateOrganization(orgId, request);

        // Then
        verify(commandGateway).sendAndWait(any(UpdateOrganizationCommand.class));
    }

    @Test
    void shouldUpdateOrganization_withPartialFields() {
        // Given
        UUID orgId = UUID.randomUUID();
        UpdateOrganizationRequest request = new UpdateOrganizationRequest(
            "Partial Update",
            "partial@example.com",
            null
        );

        // When
        commandService.updateOrganization(orgId, request);

        // Then
        verify(commandGateway).sendAndWait(any(UpdateOrganizationCommand.class));
    }

    @Test
    void shouldDeactivateOrganization_whenValidId() {
        // Given
        UUID orgId = UUID.randomUUID();

        // When
        commandService.deactivateOrganization(orgId);

        // Then
        verify(commandGateway).sendAndWait(any(DeactivateOrganizationCommand.class));
    }
}
