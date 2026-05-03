package io.orkidea.organizations.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.orkidea.organizations.config.AxonTestConfig;
import io.orkidea.organizations.dto.CreateOrganizationRequest;
import io.orkidea.organizations.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AxonTestConfig.class)
class InternalOrganizationControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrganizationRepository organizationRepository;

    @BeforeEach
    void cleanUp() {
        organizationRepository.deleteAll();
    }

    @Test
    void shouldCreateOrganization_withoutAuthToken() throws Exception {
        var request = new CreateOrganizationRequest("Acme Corp", "billing@acme.com", null);

        mockMvc.perform(post("/internal/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/organizations/")))
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name").value("Acme Corp"))
            .andExpect(jsonPath("$.contactEmail").value("billing@acme.com"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturn400_whenNameBlank() throws Exception {
        var request = new CreateOrganizationRequest("", "billing@acme.com", null);

        mockMvc.perform(post("/internal/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenContactEmailInvalid() throws Exception {
        var request = new CreateOrganizationRequest("Acme Corp", "not-an-email", null);

        mockMvc.perform(post("/internal/organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
