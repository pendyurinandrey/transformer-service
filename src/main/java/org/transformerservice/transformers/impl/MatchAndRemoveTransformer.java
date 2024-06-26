package org.transformerservice.transformers.impl;

import org.transformerservice.annotations.TransformerSpec;
import org.transformerservice.exceptions.InvalidTransformerConfigurationException;
import org.transformerservice.transformers.Transformer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.PatternSyntaxException;

@Service
@TransformerSpec(groupId = "matchers", transformerId = "match-and-remove")
public class MatchAndRemoveTransformer implements Transformer {
    public static final String REGEXP_PROPERTY_NAME = "regexp";

    @Override
    public String transform(String source, Map<String, String> properties) {
        if (source == null) {
            return source;
        }
        if (properties == null || properties.get(REGEXP_PROPERTY_NAME) == null) {
            throw new InvalidTransformerConfigurationException(
                    String.format("'%s' property has not been provided", REGEXP_PROPERTY_NAME));
        }
        try {
            return source.replaceAll(properties.get(REGEXP_PROPERTY_NAME), "");
        } catch (PatternSyntaxException ex) {
            throw new InvalidTransformerConfigurationException("Wrong pattern", ex);
        }
    }
}
