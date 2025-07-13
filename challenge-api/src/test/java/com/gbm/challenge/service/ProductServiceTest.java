package com.gbm.challenge.service;

import com.gbm.challenge.dto.product.ProductRequestDTO;
import com.gbm.challenge.dto.product.ProductResponseDTO;
import com.gbm.challenge.model.Category;
import com.gbm.challenge.model.Product;
import com.gbm.challenge.repository.CategoryRepository;
import com.gbm.challenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProductWithValidCategory() {
        ProductRequestDTO request = new ProductRequestDTO("Produto Teste", "Descrição", 99.9, 1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Categoria");

        Product product = new Product();
        product.setId(10L);
        product.setName("Produto Teste");
        product.setDescription("Descrição");
        product.setPrice(99.9);
        product.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("Produto Teste", response.getName());
        assertEquals("Categoria", response.getCategory().getName());
    }

    @Test
    void shouldThrowExceptionIfCategoryNotFoundOnCreate() {
        ProductRequestDTO request = new ProductRequestDTO("Produto", "Desc", 10.0, 99L);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(request);
        });

        assertEquals("Categoria não encontrada", ex.getMessage());
    }

    @Test
    void shouldReturnAllProducts() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Cat");
        Product p = new Product();
        p.setId(1L);
        p.setName("P1");
        p.setCategory(category);

        when(productRepository.findAll()).thenReturn(List.of(p));

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).getName());
    }

    @Test
    void shouldReturnProductById() {
        Product product = new Product();
        product.setId(1L);
        product.setName("P1");
        product.setCategory(new Category());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.getProductById(1L);

        assertEquals("P1", response.getName());
    }

    @Test
    void shouldUpdateProductIfExists() {
        Product existing = new Product();
        existing.setId(1L);
        Category cat = new Category();
        cat.setId(2L);
        cat.setName("Nova");

        ProductRequestDTO dto = new ProductRequestDTO("Atualizado", "Desc", 123.0, 2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(cat));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponseDTO updated = productService.updateProduct(1L, dto);

        assertEquals("Atualizado", updated.getName());
        assertEquals(123.0, updated.getPrice());
        assertEquals("Nova", updated.getCategory().getName());
    }

    @Test
    void shouldDeleteProductIfExists() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionIfProductNotFoundOnDelete() {
        when(productRepository.existsById(42L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(42L);
        });

        assertEquals("Produto não encontrado", ex.getMessage());
    }
}
