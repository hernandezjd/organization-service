package io.orkidea.organizations.projection;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import io.orkidea.organizations.event.OrganizationCreatedEvent;
import io.orkidea.organizations.event.OrganizationDeactivatedEvent;
import io.orkidea.organizations.event.OrganizationUpdatedEvent;
import io.orkidea.organizations.model.OrganizationEntity;
import io.orkidea.organizations.model.OrganizationStatus;
import io.orkidea.organizations.repository.OrganizationRepository;

import java.time.Instant;

/**
 * Projection handler that materializes organization events into the read-side PostgreSQL database.
 * Implements the event-driven projection pattern: events are consumed and the projection state is updated.
 *
 * Idempotency: The create handler includes an existsById check to handle event replay safely.
 * In a true event-processor service, every create event should be idempotent.
 * In a single-service design, this is a defensive measure.
 */
@Component
public class OrganizationProjection {

    private final OrganizationRepository repository;

    public OrganizationProjection(OrganizationRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(OrganizationCreatedEvent event) {
        // Idempotency guard: if the organization already exists, skip (event replay safety)
        if (repository.existsById(event.organizationId())) {
            return;
        }

        OrganizationEntity entity = new OrganizationEntity();
        entity.setOrganizationId(event.organizationId());
        entity.setName(event.name());
        entity.setContactEmail(event.contactEmail());

        if (event.address() != null) {
            entity.setStreet(event.address().street());
            entity.setCity(event.address().city());
            entity.setState(event.address().state());
            entity.setPostalCode(event.address().postalCode());
            entity.setCountry(event.address().country());
        }

        entity.setStatus(OrganizationStatus.ACTIVE);
        entity.setCreatedAt(event.occurredAt());
        entity.setModifiedAt(event.occurredAt());

        repository.save(entity);
    }

    @EventHandler
    public void on(OrganizationUpdatedEvent event) {
        repository.findById(event.organizationId()).ifPresent(entity -> {
            entity.setName(event.name());
            entity.setContactEmail(event.contactEmail());

            if (event.address() != null) {
                entity.setStreet(event.address().street());
                entity.setCity(event.address().city());
                entity.setState(event.address().state());
                entity.setPostalCode(event.address().postalCode());
                entity.setCountry(event.address().country());
            } else {
                // Clear address if null
                entity.setStreet(null);
                entity.setCity(null);
                entity.setState(null);
                entity.setPostalCode(null);
                entity.setCountry(null);
            }

            entity.setModifiedAt(event.occurredAt());
            repository.save(entity);
        });
    }

    @EventHandler
    public void on(OrganizationDeactivatedEvent event) {
        repository.findById(event.organizationId()).ifPresent(entity -> {
            entity.setStatus(OrganizationStatus.INACTIVE);
            entity.setModifiedAt(event.occurredAt());
            repository.save(entity);
        });
    }
}
