package io.orkidea.organizations.projection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.orkidea.organizations.command.AddressValue;
import io.orkidea.organizations.event.OrganizationCreatedEvent;
import io.orkidea.organizations.event.OrganizationDeactivatedEvent;
import io.orkidea.organizations.event.OrganizationUpdatedEvent;
import io.orkidea.organizations.model.OrganizationEntity;
import io.orkidea.organizations.model.OrganizationStatus;
import io.orkidea.organizations.repository.OrganizationRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrganizationProjection event handler.
 * Tests event handler behavior, idempotency, and projection state updates.
 */
@ExtendWith(MockitoExtension.class)
class OrganizationProjectionTest {

    @Mock
    private OrganizationRepository repository;

    private OrganizationProjection projection;

    private static final String ORG_ID = UUID.randomUUID().toString();
    private static final String ORG_NAME = "Acme Corporation";
    private static final String ORG_EMAIL = "contact@acme.com";
    private static final AddressValue ADDRESS = new AddressValue(
            "123 Innovation Drive",
            "San Francisco",
            "CA",
            "94105",
            "US"
    );

    @BeforeEach
    void setUp() {
        projection = new OrganizationProjection(repository);
    }

    @Test
    void shouldCreateEntity_whenOrganizationCreatedEvent() {
        OrganizationCreatedEvent event = new OrganizationCreatedEvent(
                ORG_ID, ORG_NAME, ORG_EMAIL, ADDRESS, Instant.now()
        );

        when(repository.existsById(ORG_ID)).thenReturn(false);

        projection.on(event);

        verify(repository).save(any(OrganizationEntity.class));
    }

    @Test
    void shouldSkipCreate_whenEntityAlreadyExists() {
        OrganizationCreatedEvent event = new OrganizationCreatedEvent(
                ORG_ID, ORG_NAME, ORG_EMAIL, ADDRESS, Instant.now()
        );

        when(repository.existsById(ORG_ID)).thenReturn(true);

        projection.on(event);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldUpdateEntity_whenOrganizationUpdatedEvent() {
        OrganizationEntity existingEntity = new OrganizationEntity();
        existingEntity.setOrganizationId(ORG_ID);
        existingEntity.setName(ORG_NAME);
        existingEntity.setContactEmail(ORG_EMAIL);
        existingEntity.setStatus(OrganizationStatus.ACTIVE);

        OrganizationUpdatedEvent event = new OrganizationUpdatedEvent(
                ORG_ID,
                "Acme Inc.",
                "billing@acme.com",
                ADDRESS,
                Instant.now()
        );

        when(repository.findById(ORG_ID)).thenReturn(Optional.of(existingEntity));

        projection.on(event);

        verify(repository).save(any(OrganizationEntity.class));
    }

    @Test
    void shouldDeactivateEntity_whenOrganizationDeactivatedEvent() {
        OrganizationEntity existingEntity = new OrganizationEntity();
        existingEntity.setOrganizationId(ORG_ID);
        existingEntity.setName(ORG_NAME);
        existingEntity.setStatus(OrganizationStatus.ACTIVE);

        OrganizationDeactivatedEvent event = new OrganizationDeactivatedEvent(
                ORG_ID,
                Instant.now()
        );

        when(repository.findById(ORG_ID)).thenReturn(Optional.of(existingEntity));

        projection.on(event);

        verify(repository).save(any(OrganizationEntity.class));
    }

    @Test
    void shouldNotUpdateNonexistentEntity_onUpdateEvent() {
        OrganizationUpdatedEvent event = new OrganizationUpdatedEvent(
                ORG_ID,
                "Updated Name",
                "updated@email.com",
                ADDRESS,
                Instant.now()
        );

        when(repository.findById(ORG_ID)).thenReturn(Optional.empty());

        projection.on(event);

        verify(repository, never()).save(any());
    }
}
