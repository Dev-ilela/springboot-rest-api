package com.gbm.challenge.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String description;

    public CategoryResponseDTO(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
