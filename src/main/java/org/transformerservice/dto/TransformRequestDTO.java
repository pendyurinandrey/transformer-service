package org.transformerservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransformRequestDTO {
    @Size(min = 1, max = 20)
    @Valid
    private List<ElementDTO> elements;
}
