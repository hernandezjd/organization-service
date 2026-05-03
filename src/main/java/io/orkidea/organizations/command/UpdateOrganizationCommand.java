package io.orkidea.organizations.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * Command to update an existing organization.
 * Updates mutable fields: name, contactEmail, address.
 * Status remains managed via DeactivateOrganizationCommand only.
 */
public record UpdateOrganizationCommand(
        @TargetAggregateIdentifier String organizationId,
        String name,
        String contactEmail,
        AddressValue address
) {
    public UpdateOrganizationCommand {
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
