package org.transformerservice.transformers.impl;

import org.junit.jupiter.api.Test;
import org.transformerservice.exceptions.InvalidTransformerConfigurationException;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MatchAndReplaceTransformerTest {

    @Test
    public void testThatNullStringWillBeReturnedAsIs() {
        var transformer = new MatchAndReplaceTransformer();
        assertThat(transformer.transform(null, Collections.emptyMap())).isNull();
    }

    @Test
    public void testThatExceptionWillBeRaisedIfPropertiesMapIsNull() {
        var transformer = new MatchAndReplaceTransformer();
        assertThatThrownBy(() -> transformer.transform("1234", null))
                .isInstanceOf(InvalidTransformerConfigurationException.class);
    }

    @Test
    public void testThatIfRegexpIsInvalidThenExceptionWillBeRaised() {
        var transformer = new MatchAndReplaceTransformer();
        assertThatThrownBy(() -> transformer.transform("1234", Map.of("regexp", "\\")))
                .isInstanceOf(InvalidTransformerConfigurationException.class);
    }

    @Test
    public void testThatProvidedValueWillBeReplacedByPattern() {
        var transformer = new MatchAndReplaceTransformer();
        var properties = Map.of(
                "regexp", "^\\d+",
                "replacement", "aaa"
        );
        assertThat(transformer.transform("123d123", properties)).isEqualTo("aaad123");
    }
}
