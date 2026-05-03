package io.orkidea.organizations.dto;

/**
 * Error response body for API errors.
 * Includes optional fields for subscription-specific and quota-related errors.
 */
public record ErrorResponse(
        String error,
        String message,
        String subscriptionStatus,
        String quotaType
) {
    /**
     * Constructor for basic error response.
     */
    public ErrorResponse(String error) {
        this(error, null, null, null);
    }

    /**
     * Constructor for error response with message.
     */
    public ErrorResponse(String error, String message) {
        this(error, message, null, null);
    }
}
