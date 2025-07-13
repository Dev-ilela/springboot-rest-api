package com.gbm.challenge.dto.product;

import com.gbm.challenge.dto.category.CategoryResponseDTO;
import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private CategoryResponseDTO category;

}
