package com.projectaccounts.organizations.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectaccounts.organizations.config.AxonTestConfig;
import com.projectaccounts.organizations.dto.Address;
import com.projectaccounts.organizations.dto.CreateOrganizationRequest;
import com.projectaccounts.organizations.dto.UpdateOrganizationRequest;
import com.projectaccounts.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AxonTestConfig.class)
@WithMockUser
class OrganizationValidationIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrganizationRepository organizationRepository;

    @BeforeEach
    void cleanUp() {
        organizationRepository.deleteAll();
    }

    @Test
    void shouldReturn400_whenNameIsMissing() throws Exception {
        var request = new CreateOrganizationRequest(null, "contact@acme.com", null);

        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn400_whenContactEmailIsMissing() throws Exception {
        var request = new CreateOrganizationRequest("Acme Corp", null, null);

        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn404_whenOrganizationNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/organizations/{id}", randomId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("ORGANIZATION_NOT_FOUND"));
    }

    @Test
    void shouldReturn404_whenUpdatingNonExistentOrganization() throws Exception {
        UUID randomId = UUID.randomUUID();
        var request = new UpdateOrganizationRequest("New Name", "new@example.com", null);

        mockMvc.perform(put("/organizations/{id}", randomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/organizations"))
            .andExpect(status().isUnauthorized());
    }
}
