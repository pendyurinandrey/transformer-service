package org.transformerservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElementDTO {
    @NotEmpty
    private String value;

    @Size(min = 1, max = 20)
    @NotNull
    @Valid
    private List<TransformerConfigDTO> transformers;
}
