package com.gbm.challenge.service;

import com.gbm.challenge.dto.category.CategoryRequestDTO;
import com.gbm.challenge.dto.category.CategoryResponseDTO;
import com.gbm.challenge.model.Category;
import com.gbm.challenge.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateCategorySuccessfully() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Tech");
        request.setDescription("Tecnologia");

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Tech");
        saved.setDescription("Tecnologia");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponseDTO response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("Tech", response.getName());
    }

    @Test
    void shouldReturnAllCategories() {
        Category c1 = new Category();
        c1.setId(1L);
        c1.setName("A");
        Category c2 = new Category();
        c2.setId(2L);
        c2.setName("B");

        when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getName());
    }

    @Test
    void shouldReturnCategoryById() {
        Category cat = new Category();
        cat.setId(5L);
        cat.setName("Alimentos");

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(cat));

        CategoryResponseDTO response = categoryService.getCategoryById(5L);

        assertEquals("Alimentos", response.getName());
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFoundById() {
        when(categoryRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(42L);
        });

        assertEquals("Categoria não encontrada", ex.getMessage());
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Old");

        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Updated");
        request.setDescription("Nova descrição");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoryResponseDTO updated = categoryService.updateCategory(1L, request);

        assertEquals("Updated", updated.getName());
        assertEquals("Nova descrição", updated.getDescription());
    }

    @Test
    void shouldDeleteCategorySuccessfully() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFoundOnDelete() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(99L);
        });

        assertEquals("Categoria não encontrada", ex.getMessage());
    }
}
