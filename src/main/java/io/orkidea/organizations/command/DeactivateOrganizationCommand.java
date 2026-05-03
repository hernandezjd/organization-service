package io.orkidea.organizations.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * Command to deactivate (soft-delete) an organization.
 * Automatically cancels the organization's subscription (handled by Subscription Service).
 */
public record DeactivateOrganizationCommand(
        @TargetAggregateIdentifier String organizationId
) {
    public DeactivateOrganizationCommand {
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("organizationId must not be null or blank");
        }
    }
}
