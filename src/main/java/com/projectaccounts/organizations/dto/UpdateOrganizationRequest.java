package com.projectaccounts.organizations.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for updating an existing organization.
 * All fields are optional to support partial updates.
 * If provided, name and contactEmail are validated as non-blank.
 */
public record UpdateOrganizationRequest(
        @NotBlank(message = "name must not be blank if provided")
        String name,

        @Email(message = "contactEmail must be a valid email address if provided")
        String contactEmail,

        Address address
) {
}
