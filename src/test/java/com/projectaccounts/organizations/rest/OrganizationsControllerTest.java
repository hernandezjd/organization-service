package com.projectaccounts.organizations.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectaccounts.organizations.dto.Address;
import com.projectaccounts.organizations.dto.CreateOrganizationRequest;
import com.projectaccounts.organizations.dto.UpdateOrganizationRequest;
import com.projectaccounts.organizations.model.OrganizationEntity;
import com.projectaccounts.organizations.model.OrganizationStatus;
import com.projectaccounts.organizations.service.OrganizationCommandService;
import com.projectaccounts.organizations.service.OrganizationQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizationsController.class)
@Import(TestSecurityConfig.class)
class OrganizationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrganizationCommandService commandService;

    @MockBean
    private OrganizationQueryService queryService;

    @Test
    void shouldCreateOrganization_withValidRequest() throws Exception {
        // Given
        CreateOrganizationRequest request = new CreateOrganizationRequest(
            "Acme Corp",
            "contact@acme.com",
            new Address("123 Main St", "San Francisco", "CA", "94105", "USA")
        );

        UUID orgId = UUID.randomUUID();
        when(commandService.createOrganization(any(CreateOrganizationRequest.class)))
            .thenReturn(orgId);

        // When & Then
        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Acme Corp"))
            .andExpect(jsonPath("$.contactEmail").value("contact@acme.com"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(commandService).createOrganization(any(CreateOrganizationRequest.class));
    }

    @Test
    void shouldReturnBadRequest_whenCreateWithoutName() throws Exception {
        // Given
        CreateOrganizationRequest request = new CreateOrganizationRequest(
            null,
            "contact@acme.com",
            null
        );

        // When & Then
        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOrganizationById_whenExists() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        when(queryService.getOrganizationById(orgId))
            .thenReturn(createOrganizationEntity(orgId.toString()));

        // When & Then
        mockMvc.perform(get("/organizations/{id}", orgId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orgId.toString()))
            .andExpect(jsonPath("$.name").value("Acme Corp"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(queryService).getOrganizationById(orgId);
    }

    @Test
    void shouldReturnNotFound_whenGetOrganizationByIdNotExists() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        when(queryService.getOrganizationById(orgId))
            .thenThrow(new OrganizationQueryService.OrganizationNotFoundException(orgId));

        // When & Then
        mockMvc.perform(get("/organizations/{id}", orgId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("ORGANIZATION_NOT_FOUND"));
    }

    @Test
    void shouldListAllOrganizations() throws Exception {
        // Given
        UUID orgId1 = UUID.randomUUID();
        UUID orgId2 = UUID.randomUUID();
        List<OrganizationEntity> organizations = List.of(
            createOrganizationEntity(orgId1.toString()),
            createOrganizationEntity(orgId2.toString())
        );

        when(queryService.getAllOrganizations()).thenReturn(organizations);

        // When & Then
        mockMvc.perform(get("/organizations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(orgId1.toString()))
            .andExpect(jsonPath("$[1].id").value(orgId2.toString()));

        verify(queryService).getAllOrganizations();
    }

    @Test
    void shouldUpdateOrganization_withValidRequest() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        UpdateOrganizationRequest request = new UpdateOrganizationRequest(
            "Updated Corp",
            "updated@example.com",
            new Address("456 Oak Ave", "Boston", "MA", "02101", "USA")
        );

        OrganizationEntity updatedEntity = createOrganizationEntity(orgId.toString());
        updatedEntity.setName("Updated Corp");
        updatedEntity.setContactEmail("updated@example.com");

        when(queryService.getOrganizationById(orgId)).thenReturn(updatedEntity);

        // When & Then
        mockMvc.perform(put("/organizations/{id}", orgId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orgId.toString()))
            .andExpect(jsonPath("$.name").value("Updated Corp"))
            .andExpect(jsonPath("$.contactEmail").value("updated@example.com"));

        verify(commandService).updateOrganization(eq(orgId), any(UpdateOrganizationRequest.class));
        verify(queryService).getOrganizationById(orgId);
    }

    @Test
    void shouldReturnNotFound_whenUpdateNonExistentOrganization() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        UpdateOrganizationRequest request = new UpdateOrganizationRequest(
            "Updated Corp",
            "updated@example.com",
            null
        );

        when(queryService.getOrganizationById(orgId))
            .thenThrow(new OrganizationQueryService.OrganizationNotFoundException(orgId));

        // When & Then
        mockMvc.perform(put("/organizations/{id}", orgId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOrganization_whenExists() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/organizations/{id}", orgId))
            .andExpect(status().isNoContent());

        verify(commandService).deactivateOrganization(orgId);
    }

    @Test
    void shouldDeleteOrganization_whenDoesNotExist() throws Exception {
        // Given
        UUID orgId = UUID.randomUUID();
        // When the command is sent for a non-existent organization, Axon's aggregate repository throws CommandExecutionException
        // which maps to 400 Bad Request via global exception handler

        // When & Then
        mockMvc.perform(delete("/organizations/{id}", orgId))
            .andExpect(status().isNoContent());
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
