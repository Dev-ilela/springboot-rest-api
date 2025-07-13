package com.gbm.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbm.challenge.dto.category.CategoryRequestDTO;
import com.gbm.challenge.dto.category.CategoryResponseDTO;
import com.gbm.challenge.repository.UserRepository;
import com.gbm.challenge.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnListOfCategories() throws Exception {
        CategoryResponseDTO dto1 = new CategoryResponseDTO(1L, "Eletrônicos", "Tech");
        CategoryResponseDTO dto2 = new CategoryResponseDTO(2L, "Roupas", "Moda");

        when(categoryService.getAllCategories()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/categories")).andExpect(status().isOk()).andExpect(jsonPath("$.size()").value(2)).andExpect(jsonPath("$[0].name").value("Eletrônicos")).andExpect(jsonPath("$[1].name").value("Roupas"));
    }

    @Test
    void shouldReturnCategoryById() throws Exception {
        CategoryResponseDTO dto = new CategoryResponseDTO(1L, "Eletrônicos", "Tech");

        when(categoryService.getCategoryById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Eletrônicos"));
    }

    @Test
    void shouldCreateCategory() throws Exception {
        CategoryRequestDTO req = new CategoryRequestDTO("Eletrônicos", "Tech");
        CategoryResponseDTO res = new CategoryResponseDTO(1L, "Eletrônicos", "Tech");

        when(categoryService.createCategory(req)).thenReturn(res);

        mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Eletrônicos"));
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        CategoryRequestDTO req = new CategoryRequestDTO("Eletrônicos Updated", "Tech Updated");
        CategoryResponseDTO res = new CategoryResponseDTO(1L, "Eletrônicos Updated", "Tech Updated");

        when(categoryService.updateCategory(eq(1L), any(CategoryRequestDTO.class))).thenReturn(res);

        mockMvc.perform(put("/api/categories/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req))).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Eletrônicos Updated")).andExpect(jsonPath("$.description").value("Tech Updated"));
    }

    @Test
    void shouldDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/1")).andExpect(status().isNoContent());
    }


}
