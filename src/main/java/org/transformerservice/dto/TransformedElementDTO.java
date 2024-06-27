package org.transformerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformedElementDTO {
    private String sourceValue;
    private String transformedValue;
}
