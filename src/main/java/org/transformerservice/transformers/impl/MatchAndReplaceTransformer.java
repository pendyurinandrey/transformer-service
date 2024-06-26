package org.transformerservice.transformers.impl;

import org.springframework.stereotype.Service;
import org.transformerservice.annotations.TransformerSpec;
import org.transformerservice.exceptions.InvalidTransformerConfigurationException;
import org.transformerservice.transformers.Transformer;

import java.util.Map;
import java.util.regex.PatternSyntaxException;

@Service
@TransformerSpec(groupId = "matchers", transformerId = "match-and-replace")
public class MatchAndReplaceTransformer implements Transformer {

    public static final String REGEXP_PROPERTY_NAME = "regexp";
    public static final String REPLACEMENT_PROPERTY_NAME = "replacement";

    @Override
    public String transform(String source, Map<String, String> properties) {
        if (source == null) {
            return source;
        }
        if (properties == null) {
            throw new InvalidTransformerConfigurationException("Required properties have not been provided");
        }
        var regexpValue = properties.get(REGEXP_PROPERTY_NAME);
        var replacementValue = properties.get(REPLACEMENT_PROPERTY_NAME);
        if (regexpValue == null) {
            throw new InvalidTransformerConfigurationException(
                    String.format("'%s' property has not been provided", REGEXP_PROPERTY_NAME));
        }

        if (replacementValue == null) {
            throw new InvalidTransformerConfigurationException(
                    String.format("'%s' property has not been provided", REPLACEMENT_PROPERTY_NAME));
        }
        try {
            return source.replaceAll(regexpValue, replacementValue);
        } catch (PatternSyntaxException ex) {
            throw new InvalidTransformerConfigurationException("Wrong pattern", ex);
        }
    }
}
