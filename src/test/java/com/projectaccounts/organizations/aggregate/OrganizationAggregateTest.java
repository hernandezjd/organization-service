package com.projectaccounts.organizations.aggregate;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projectaccounts.organizations.command.AddressValue;
import com.projectaccounts.organizations.command.CreateOrganizationCommand;
import com.projectaccounts.organizations.command.DeactivateOrganizationCommand;
import com.projectaccounts.organizations.command.UpdateOrganizationCommand;
import com.projectaccounts.organizations.event.OrganizationCreatedEvent;
import com.projectaccounts.organizations.event.OrganizationDeactivatedEvent;
import com.projectaccounts.organizations.event.OrganizationUpdatedEvent;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OrganizationAggregateTest {

    private FixtureConfiguration<OrganizationAggregate> fixture;

    private static final String ORG_ID = UUID.randomUUID().toString();
    private static final String ORG_NAME = "Acme Corporation";
    private static final String ORG_EMAIL = "contact@acme.com";
    private static final AddressValue ORG_ADDRESS = new AddressValue(
            "123 Innovation Drive",
            "San Francisco",
            "CA",
            "94105",
            "US"
    );

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(OrganizationAggregate.class);
    }

    @Test
    void shouldCreateOrganization_whenValidCommand() {
        fixture.givenNoPriorActivity()
                .when(new CreateOrganizationCommand(ORG_ID, ORG_NAME, ORG_EMAIL, ORG_ADDRESS))
                .expectSuccessfulHandlerExecution();
    }

    @Test
    void shouldUpdateOrganization_whenActive() {
        AddressValue updatedAddress = new AddressValue(
                "456 Updated Street",
                "Los Angeles",
                "CA",
                "90001",
                "US"
        );
        fixture.given(organizationCreatedEvent())
                .when(new UpdateOrganizationCommand(ORG_ID, "Acme Inc.", "billing@acme.com", updatedAddress))
                .expectSuccessfulHandlerExecution();
    }

    @Test
    void shouldDeactivateOrganization_whenActive() {
        fixture.given(organizationCreatedEvent())
                .when(new DeactivateOrganizationCommand(ORG_ID))
                .expectSuccessfulHandlerExecution();
    }

    @Test
    void shouldRejectUpdate_whenInactive() {
        fixture.given(organizationCreatedEvent(), organizationDeactivatedEvent())
                .when(new UpdateOrganizationCommand(ORG_ID, "Updated Name", "new@email.com", ORG_ADDRESS))
                .expectException(IllegalStateException.class);
    }

    @Test
    void shouldRejectDeactivate_whenAlreadyInactive() {
        fixture.given(organizationCreatedEvent(), organizationDeactivatedEvent())
                .when(new DeactivateOrganizationCommand(ORG_ID))
                .expectException(IllegalStateException.class);
    }

    @Test
    void shouldRejectCreate_whenNameBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateOrganizationCommand(ORG_ID, "", ORG_EMAIL, ORG_ADDRESS)
        );
    }

    @Test
    void shouldRejectCreate_whenEmailBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateOrganizationCommand(ORG_ID, ORG_NAME, "", ORG_ADDRESS)
        );
    }

    @Test
    void shouldRejectCreate_whenIdBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateOrganizationCommand("", ORG_NAME, ORG_EMAIL, ORG_ADDRESS)
        );
    }

    // Helpers for prior-state events
    private OrganizationCreatedEvent organizationCreatedEvent() {
        return new OrganizationCreatedEvent(ORG_ID, ORG_NAME, ORG_EMAIL, ORG_ADDRESS, Instant.now());
    }

    private OrganizationDeactivatedEvent organizationDeactivatedEvent() {
        return new OrganizationDeactivatedEvent(ORG_ID, Instant.now());
    }
}
