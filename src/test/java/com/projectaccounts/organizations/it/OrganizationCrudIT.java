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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AxonTestConfig.class)
@WithMockUser
class OrganizationCrudIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrganizationRepository organizationRepository;

    @BeforeEach
    void cleanUp() {
        organizationRepository.deleteAll();
    }

    @Test
    void shouldCreateOrganization_andReturnCreated() throws Exception {
        var request = new CreateOrganizationRequest(
            "Acme Corp",
            "contact@acme.com",
            new Address("123 Main St", "San Francisco", "CA", "94105", "USA")
        );

        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/organizations/")))
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value("Acme Corp"))
            .andExpect(jsonPath("$.contactEmail").value("contact@acme.com"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.address.street").value("123 Main St"))
            .andExpect(jsonPath("$.address.city").value("San Francisco"));
    }

    @Test
    void shouldCreateOrganization_andRetrieveById() throws Exception {
        var request = new CreateOrganizationRequest("Globex", "info@globex.com", null);

        MvcResult createResult = mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(get("/organizations/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Globex"))
            .andExpect(jsonPath("$.contactEmail").value("info@globex.com"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.modifiedAt").isNotEmpty());
    }

    @Test
    void shouldListAllOrganizations() throws Exception {
        var req1 = new CreateOrganizationRequest("Org Alpha", "alpha@example.com", null);
        var req2 = new CreateOrganizationRequest("Org Beta", "beta@example.com", null);

        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/organizations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].name", containsInAnyOrder("Org Alpha", "Org Beta")));
    }

    @Test
    void shouldUpdateOrganization() throws Exception {
        var createRequest = new CreateOrganizationRequest("Old Name", "old@example.com", null);
        MvcResult createResult = mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        var updateRequest = new UpdateOrganizationRequest(
            "New Name",
            "new@example.com",
            new Address("456 Oak Ave", "Boston", "MA", "02101", "USA")
        );

        mockMvc.perform(put("/organizations/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("New Name"))
            .andExpect(jsonPath("$.contactEmail").value("new@example.com"))
            .andExpect(jsonPath("$.address.street").value("456 Oak Ave"));
    }

    @Test
    void shouldDeactivateOrganization() throws Exception {
        var request = new CreateOrganizationRequest("Doomed Corp", "doom@example.com", null);
        MvcResult createResult = mockMvc.perform(post("/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(delete("/organizations/{id}", id))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/organizations/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("INACTIVE"));
    }
}
