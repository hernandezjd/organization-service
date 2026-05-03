package io.orkidea.organizations.event;

import java.time.Instant;

/**
 * Event published when an organization is deactivated (soft-deleted).
 * Automatically triggers subscription cancellation.
 */
public record OrganizationDeactivatedEvent(
        String organizationId,
        Instant occurredAt
) {
    public OrganizationDeactivatedEvent {
        if (organizationId == null || organizationId.isBlank()) {
            throw new IllegalArgumentException("organizationId must not be null or blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt must not be null");
        }
    }
}
