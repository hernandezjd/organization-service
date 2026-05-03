package io.orkidea.organizations.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for creating a new organization.
 * Name and contactEmail are required; address is optional.
 */
public record CreateOrganizationRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "contactEmail is required")
        @Email(message = "contactEmail must be a valid email address")
        String contactEmail,

        Address address
) {
}
