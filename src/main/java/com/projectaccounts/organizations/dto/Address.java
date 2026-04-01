package com.projectaccounts.organizations.dto;

/**
 * Address value object for organization requests and responses.
 */
public record Address(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) {
}
