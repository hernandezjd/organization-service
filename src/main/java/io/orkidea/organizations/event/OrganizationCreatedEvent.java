package io.orkidea.organizations.event;

import io.orkidea.organizations.command.AddressValue;

import java.time.Instant;

/**
 * Event published when a new organization is created.
 * Automatically triggers trial subscription creation.
 */
public record OrganizationCreatedEvent(
        String organizationId,
        String name,
        String contactEmail,
        AddressValue address,
        Instant occurredAt
) {
    public OrganizationCreatedEvent {
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("organizationId must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (contactEmail == null || contactEmail.isBlank()) {
            throw new IllegalArgumentException("contactEmail must not be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt must not be null");
        }
        // address is optional
    }
}
