package com.projectaccounts.organizations.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * Command to create a new organization.
 * Validates that name and contactEmail are non-blank.
 */
public record CreateOrganizationCommand(
        @TargetAggregateIdentifier String organizationId,
        String name,
        String contactEmail,
        AddressValue address
) {
    public CreateOrganizationCommand {
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("organizationId must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (contactEmail == null || contactEmail.isBlank()) {
            throw new IllegalArgumentException("contactEmail must not be null or blank");
        }
        // address is optional
    }
}
