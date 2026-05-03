package io.orkidea.organizations.model;

/**
 * Enumeration of organization status values.
 * ACTIVE: organization is operational with an active subscription.
 * INACTIVE: organization is soft-deleted (data retained), all workspaces read-only.
 */
public enum OrganizationStatus {
    ACTIVE,
    INACTIVE
}
