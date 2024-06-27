package org.transformerservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TransformRequestDTO {
    @Size(min = 1, max = 20)
    @Valid
    private List<ElementDTO> elements;
}
