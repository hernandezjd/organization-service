package com.projectaccounts.organizations.service;

import com.projectaccounts.organizations.command.CreateOrganizationCommand;
import com.projectaccounts.organizations.command.UpdateOrganizationCommand;
import com.projectaccounts.organizations.command.DeactivateOrganizationCommand;
import com.projectaccounts.organizations.command.AddressValue;
import com.projectaccounts.organizations.dto.CreateOrganizationRequest;
import com.projectaccounts.organizations.dto.UpdateOrganizationRequest;
import com.projectaccounts.organizations.dto.Address;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrganizationCommandService {

    private final CommandGateway commandGateway;

    public OrganizationCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    public UUID createOrganization(CreateOrganizationRequest request) {
        String organizationId = UUID.randomUUID().toString();
        AddressValue addressValue = request.address() != null
            ? new AddressValue(
                request.address().street(),
                request.address().city(),
                request.address().state(),
                request.address().postalCode(),
                request.address().country()
            )
            : null;

        CreateOrganizationCommand command = new CreateOrganizationCommand(
            organizationId,
            request.name(),
            request.contactEmail(),
            addressValue
        );

        commandGateway.sendAndWait(command);
        return UUID.fromString(organizationId);
    }

    public void updateOrganization(UUID id, UpdateOrganizationRequest request) {
        AddressValue addressValue = request.address() != null
            ? new AddressValue(
                request.address().street(),
                request.address().city(),
                request.address().state(),
                request.address().postalCode(),
                request.address().country()
            )
            : null;

        UpdateOrganizationCommand command = new UpdateOrganizationCommand(
            id.toString(),
            request.name(),
            request.contactEmail(),
            addressValue
        );

        commandGateway.sendAndWait(command);
    }

    public void deactivateOrganization(UUID id) {
        DeactivateOrganizationCommand command = new DeactivateOrganizationCommand(id.toString());
        commandGateway.sendAndWait(command);
    }
}
