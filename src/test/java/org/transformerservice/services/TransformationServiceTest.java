package org.transformerservice.services;

import org.junit.jupiter.api.Test;
import org.transformerservice.annotations.TransformerSpec;
import org.transformerservice.dto.ElementDTO;
import org.transformerservice.dto.TransformerConfigDTO;
import org.transformerservice.exceptions.InvalidTransformerConfigurationException;
import org.transformerservice.transformers.Transformer;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TransformationServiceTest {

    @Test
    public void testThatDuplicatedTransformerCannotBeRegistered() {
        var transformers = List.of(
                new DuplicatedTransformer1(),
                new DuplicatedTransformer2()
        );

        assertThatThrownBy(() -> new TransformationService(transformers))
                .isInstanceOf(InvalidTransformerConfigurationException.class);
    }

    @Test
    public void testThatTransformerServiceConstructorWillRaiseExceptionIfTransformerDoesNotHaveTransformerSpec() {
        var transformers = List.of(
                new DuplicatedTransformer1(),
                new MissedAnnotationTransformer()
        );

        assertThatThrownBy(() -> new TransformationService(transformers))
                .isInstanceOf(InvalidTransformerConfigurationException.class);
    }

    @Test
    public void testThatTheSameTransformerCanBeAppliedMultipleTimes() {
        List<Transformer> transformers = List.of(new AppenderTransformer());
        var transformerCfg = List.of(
                new TransformerConfigDTO("appenders", "suffix-appender", Map.of("suffix", "first")),
                new TransformerConfigDTO("appenders", "suffix-appender", Map.of("suffix", "second"))
        );
        var elements = List.of(
                new ElementDTO("123", transformerCfg)
        );

        var service = new TransformationService(transformers);
        var actual = service.transform(elements);
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getSourceValue()).isEqualTo("123");
        assertThat(actual.getFirst().getTransformedValue()).isEqualTo("123firstsecond");
    }

    @TransformerSpec(groupId = "123", transformerId = "567")
    private static class DuplicatedTransformer1 implements Transformer {

        @Override
        public String transform(String source, Map<String, String> properties) {
            return source;
        }
    }

    @TransformerSpec(groupId = "123", transformerId = "567")
    private static class DuplicatedTransformer2 implements Transformer {

        @Override
        public String transform(String source, Map<String, String> properties) {
            return "2";
        }
    }

    private static class MissedAnnotationTransformer implements Transformer {

        @Override
        public String transform(String source, Map<String, String> properties) {
            return "";
        }
    }

    @TransformerSpec(groupId = "appenders", transformerId = "suffix-appender")
    private static class AppenderTransformer implements Transformer {

        @Override
        public String transform(String source, Map<String, String> properties) {
            return source + properties.get("suffix");
        }
    }
}
