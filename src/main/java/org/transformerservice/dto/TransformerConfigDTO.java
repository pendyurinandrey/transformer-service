package org.transformerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformerConfigDTO {
    @NotEmpty
    private String groupId;

    @NotEmpty
    private String transformerId;

    @Size(max = 20)
    private Map<String, String> properties = new HashMap<>();
}
