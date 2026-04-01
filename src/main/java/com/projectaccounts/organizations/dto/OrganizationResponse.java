package com.projectaccounts.organizations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Response body for organization queries and mutations.
 * All fields are read-only in the response; id, status, createdAt, and modifiedAt are server-managed.
 */
public record OrganizationResponse(
        @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
        String id,

        String name,
        String contactEmail,
        Address address,

        @JsonProperty(value = "status", access = JsonProperty.Access.READ_ONLY)
        String status,

        @JsonProperty(value = "createdAt", access = JsonProperty.Access.READ_ONLY)
        Instant createdAt,

        @JsonProperty(value = "modifiedAt", access = JsonProperty.Access.READ_ONLY)
        Instant modifiedAt
) {
}
