package org.transformerservice.transformers.impl;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CyrillicAndGreekTranslationTransformerTest {

    @Test
    public void testThatNullStringWillBeReturnedAsIs() {
        var transformer = new CyrillicAndGreekTranslationTransformer();
        assertThat(transformer.transform(null, Collections.emptyMap())).isNull();
    }

    @Test
    public void testThatOnlyLatinStringWillBeReturnedAsIs() {
        var transformer = new CyrillicAndGreekTranslationTransformer();
        assertThat(transformer.transform("abcd", null)).isEqualTo("abcd");
    }

    @Test
    public void testThatCyrillicLettersWillBeConverted() {
        var transformer = new CyrillicAndGreekTranslationTransformer();
        assertThat(transformer.transform("аябдю", null)).isEqualTo("aiabdiu");
    }

    @Test
    public void testThatGreekLettersWillBeTranslated() {
        var transformer = new CyrillicAndGreekTranslationTransformer();
        assertThat(transformer.transform("φΦηάΩ", null)).isEqualTo("fFiaO");
    }

    @Test
    public void testThatCombinationOfGreekLatinCyrillicWillBeProperlyTranslated() {
        var transformer = new CyrillicAndGreekTranslationTransformer();
        assertThat(transformer.transform("φчΦηάΩlш123", null)).isEqualTo("fchFiaOlsh123");
    }
}
