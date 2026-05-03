package io.orkidea.organizations.service;

import io.orkidea.organizations.model.OrganizationEntity;
import io.orkidea.organizations.model.OrganizationStatus;
import io.orkidea.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationQueryServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    private OrganizationQueryService queryService;

    @BeforeEach
    void setUp() {
        queryService = new OrganizationQueryService(organizationRepository);
    }

    @Test
    void shouldGetOrganizationById_whenExists() {
        // Given
        UUID orgId = UUID.randomUUID();
        OrganizationEntity entity = createOrganizationEntity(orgId.toString());

        when(organizationRepository.findById(orgId.toString()))
            .thenReturn(Optional.of(entity));

        // When
        OrganizationEntity result = queryService.getOrganizationById(orgId);

        // Then
        assertNotNull(result);
        assertEquals(orgId.toString(), result.getOrganizationId());
        assertEquals("Acme Corp", result.getName());
        verify(organizationRepository).findById(orgId.toString());
    }

    @Test
    void shouldThrowOrganizationNotFoundException_whenNotExists() {
        // Given
        UUID orgId = UUID.randomUUID();
        when(organizationRepository.findById(anyString()))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(
            OrganizationQueryService.OrganizationNotFoundException.class,
            () -> queryService.getOrganizationById(orgId)
        );
    }

    @Test
    void shouldGetAllOrganizations() {
        // Given
        OrganizationEntity entity1 = createOrganizationEntity(UUID.randomUUID().toString());
        OrganizationEntity entity2 = createOrganizationEntity(UUID.randomUUID().toString());
        List<OrganizationEntity> entities = List.of(entity1, entity2);

        when(organizationRepository.findAll()).thenReturn(entities);

        // When
        List<OrganizationEntity> result = queryService.getAllOrganizations();

        // Then
        assertEquals(2, result.size());
        verify(organizationRepository).findAll();
    }

    @Test
    void shouldReturnEmptyList_whenNoOrganizationsExist() {
        // Given
        when(organizationRepository.findAll()).thenReturn(List.of());

        // When
        List<OrganizationEntity> result = queryService.getAllOrganizations();

        // Then
        assertTrue(result.isEmpty());
    }

    private OrganizationEntity createOrganizationEntity(String orgId) {
        OrganizationEntity entity = new OrganizationEntity();
        entity.setOrganizationId(orgId);
        entity.setName("Acme Corp");
        entity.setContactEmail("contact@acme.com");
        entity.setStreet("123 Main St");
        entity.setCity("San Francisco");
        entity.setState("CA");
        entity.setPostalCode("94105");
        entity.setCountry("USA");
        entity.setStatus(OrganizationStatus.ACTIVE);
        entity.setCreatedAt(Instant.now());
        entity.setModifiedAt(Instant.now());
        return entity;
    }
}
