package com.smartclaims360.smartclaims360.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SwaggerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    void testSwaggerUiAccessible() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testApiDocsAccessible() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testOpenApiSpecContainsExpectedEndpoints() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths./health").exists())
                .andExpect(jsonPath("$.paths./claims").exists())
                .andExpect(jsonPath("$.paths./claims/{id}").exists())
                .andExpect(jsonPath("$.paths./claims/validate").exists())
                .andExpect(jsonPath("$.paths./claims/score").exists())
                .andExpect(jsonPath("$.paths./claims/{id}/summary").exists())
                .andExpect(jsonPath("$.paths./claims/{id}/route").exists());
    }

    @Test
    void testSchemaDefinitionsExist() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.Claim").exists())
                .andExpect(jsonPath("$.components.schemas.ClaimRequest").exists())
                .andExpect(jsonPath("$.components.schemas.ValidationResponse").exists())
                .andExpect(jsonPath("$.components.schemas.FraudScoreResponse").exists())
                .andExpect(jsonPath("$.components.schemas.RoutingSuggestion").exists());
    }
}
