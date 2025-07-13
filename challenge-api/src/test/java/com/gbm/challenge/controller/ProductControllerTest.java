package com.gbm.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbm.challenge.dto.product.ProductRequestDTO;
import com.gbm.challenge.dto.product.ProductResponseDTO;
import com.gbm.challenge.repository.UserRepository;
import com.gbm.challenge.service.ProductService;
import com.gbm.challenge.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnListOfProducts() throws Exception {
        ProductResponseDTO p1 = new ProductResponseDTO(2L, "Notebook", "Dell XPS", 5999.90, null);
        ProductResponseDTO p2 = new ProductResponseDTO(3L, "Camiseta", "Algodão", 49.90, null);

        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].name").value("Notebook")).andExpect(jsonPath("$[1].name").value("Camiseta"));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        ProductResponseDTO p1 = new ProductResponseDTO(2L, "Notebook", "Dell XPS", 5999.90, null);

        when(productService.getProductById(1L)).thenReturn(p1);

        mockMvc.perform(get("/api/products/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Notebook"));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequestDTO req = new ProductRequestDTO("Notebook", "Dell XPS", 5999.90, 1L);

        ProductResponseDTO res = new ProductResponseDTO();
        res.setId(1L);
        res.setName("Notebook");
        res.setDescription("Dell XPS");
        res.setPrice(5999.90);

        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(res);

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Notebook"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductRequestDTO req = new ProductRequestDTO("Notebook Updated", "Dell XPS", 6099.90, 2L);
        ProductResponseDTO res = new ProductResponseDTO();
        res.setId(2L);
        res.setName("Notebook Updated");
        res.setDescription("Dell XPS");
        res.setPrice(6099.90);

        when(productService.updateProduct(eq(2L), any(ProductRequestDTO.class))).thenReturn(res);

        mockMvc.perform(put("/api/products/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Updated"))
                .andExpect(jsonPath("$.price").value(6099.90));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/2")).andExpect(status().isNoContent());
    }
}
