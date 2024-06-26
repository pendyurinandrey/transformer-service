package org.transformerservice.transformers;

import java.util.Map;

public interface Transformer {

    String transform(String source, Map<String, String> properties);
}
