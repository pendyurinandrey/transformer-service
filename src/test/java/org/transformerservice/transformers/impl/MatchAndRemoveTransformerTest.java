package org.transformerservice.transformers.impl;

import org.junit.jupiter.api.Test;
import org.transformerservice.exceptions.InvalidTransformerConfigurationException;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MatchAndRemoveTransformerTest {

    @Test
    public void testThatNullStringWillBeReturnedAsIs() {
        var transformer = new MatchAndRemoveTransformer();
        assertThat(transformer.transform(null, Collections.emptyMap())).isNull();
    }

    @Test
    public void testThatExceptionWillBeRaisedIfRequiredPropertyDoesNotExists() {
        var transformer = new MatchAndRemoveTransformer();
        assertThatThrownBy(() -> transformer.transform("1234",
                        Map.of("unknownProperty", "value")))
                .isInstanceOf(InvalidTransformerConfigurationException.class);
    }

    @Test
    public void testThatIfRegexpIsInvalidThenExceptionWillBeRaised() {
        var transformer = new MatchAndRemoveTransformer();
        assertThatThrownBy(() -> transformer.transform("1234",
                Map.of(MatchAndRemoveTransformer.REGEXP_PROPERTY_NAME, "\\")))
                .isInstanceOf(InvalidTransformerConfigurationException.class);
    }

    @Test
    public void testThatPrefixNumbersWillBeRemovedByRegexp() {
        var transformer = new MatchAndRemoveTransformer();
        assertThat(transformer.transform("1234d123",
                Map.of(MatchAndRemoveTransformer.REGEXP_PROPERTY_NAME, "^\\d+"))).isEqualTo("d123");
    }
}
