package com.scaler.productcatalog.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductCatalogApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String login(String email, String password) throws Exception {
        String header = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getHeader("Authorization");
        return header;
    }

    @Test
    @Order(1)
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void protectedEndpointsRequireToken() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @Order(3)
    void signupValidatesInput() throws Exception {
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email")));
    }

    @Test
    @Order(4)
    void regularUserCanReadButNotWrite() throws Exception {
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"UserPass123\",\"firstName\":\"U\",\"lastName\":\"Ser\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roles[0].value").value("NON_ADMIN_USER"));
        String token = login("user@test.com", "UserPass123");

        mockMvc.perform(get("/products").header("Authorization", token)).andExpect(status().isOk());
        mockMvc.perform(post("/category").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Hacked\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    void adminCreatesCatalogueAndSearchFindsIt() throws Exception {
        String token = login("admin@test.com", "AdminPass123");

        mockMvc.perform(post("/category").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Smartphones\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/category").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Smartphones\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/products").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Samsung Galaxy A52\",\"description\":\"Mid-range phone\",\"price\":27999,\"category\":\"Smartphones\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category.title").value("Smartphones"));
        mockMvc.perform(post("/products").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"price\":-1,\"category\":\"Smartphones\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/search").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"samsung\",\"category\":\"smartphones\",\"minPrice\":10000,\"maxPrice\":80000,\"sortBy\":\"price\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Samsung Galaxy A52"))
                .andExpect(jsonPath("$.page.totalElements").value(1));

        // No sortBy: must default to id instead of failing
        mockMvc.perform(post("/search").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"galaxy\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Samsung Galaxy A52"));
    }

    @Test
    @Order(6)
    void logoutRevokesToken() throws Exception {
        String token = login("admin@test.com", "AdminPass123");
        mockMvc.perform(post("/auth/logout").header("Authorization", token)).andExpect(status().isNoContent());
        mockMvc.perform(get("/products").header("Authorization", token)).andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    void unknownProductReturns404() throws Exception {
        String token = login("admin@test.com", "AdminPass123");
        mockMvc.perform(get("/products/999999").header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 999999"));
    }
}
