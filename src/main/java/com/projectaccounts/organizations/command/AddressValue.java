package com.projectaccounts.organizations.command;

/**
 * Immutable value object representing an organization address.
 * All fields are optional.
 */
public record AddressValue(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) {
}
