package com.projectaccounts.organizations.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.projectaccounts.organizations.command.AddressValue;
import com.projectaccounts.organizations.command.CreateOrganizationCommand;
import com.projectaccounts.organizations.command.DeactivateOrganizationCommand;
import com.projectaccounts.organizations.command.UpdateOrganizationCommand;
import com.projectaccounts.organizations.event.OrganizationCreatedEvent;
import com.projectaccounts.organizations.event.OrganizationDeactivatedEvent;
import com.projectaccounts.organizations.event.OrganizationUpdatedEvent;

import java.time.Instant;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * Aggregate root for organization domain.
 * Handles organization lifecycle: creation, updates, and deactivation.
 * Enforces that status changes are only managed via deactivation.
 */
@Aggregate
public class OrganizationAggregate {

    @AggregateIdentifier
    private String organizationId;

    private String name;
    private String contactEmail;
    private AddressValue address;
    private boolean active;

    protected OrganizationAggregate() {
        // Required by Axon for reconstruction from events
    }

    @CommandHandler
    public OrganizationAggregate(CreateOrganizationCommand command) {
        apply(new OrganizationCreatedEvent(
                command.organizationId(),
                command.name(),
                command.contactEmail(),
                command.address(),
                Instant.now()
        ));
    }

    @CommandHandler
    public void handle(UpdateOrganizationCommand command) {
        if (!active) {
            throw new IllegalStateException("Cannot update an inactive organization");
        }
        apply(new OrganizationUpdatedEvent(
                command.organizationId(),
                command.name(),
                command.contactEmail(),
                command.address(),
                Instant.now()
        ));
    }

    @CommandHandler
    public void handle(DeactivateOrganizationCommand command) {
        if (!active) {
            throw new IllegalStateException("Organization is already inactive");
        }
        apply(new OrganizationDeactivatedEvent(
                command.organizationId(),
                Instant.now()
        ));
    }

    @EventSourcingHandler
    public void on(OrganizationCreatedEvent event) {
        this.organizationId = event.organizationId();
        this.name = event.name();
        this.contactEmail = event.contactEmail();
        this.address = event.address();
        this.active = true;
    }

    @EventSourcingHandler
    public void on(OrganizationUpdatedEvent event) {
        this.name = event.name();
        this.contactEmail = event.contactEmail();
        this.address = event.address();
    }

    @EventSourcingHandler
    public void on(OrganizationDeactivatedEvent event) {
        this.active = false;
    }

    // Getters for testing
    public String getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public AddressValue getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }
}
