package org.transformerservice.transformers.impl;

import org.springframework.stereotype.Service;
import org.transformerservice.annotations.TransformerSpec;
import org.transformerservice.transformers.Transformer;
import org.transformerservice.transformers.translators.Greeklish;
import ru.homyakin.iuliia.Schemas;
import ru.homyakin.iuliia.Translator;

import java.util.Map;

@Service
@TransformerSpec(groupId = "translators", transformerId = "cyrillic-and-greek")
public class CyrillicAndGreekTranslationTransformer implements Transformer {
    private final Translator cyrillicTranslator = new Translator(Schemas.ICAO_DOC_9303);

    @Override
    public String transform(String source, Map<String, String> properties) {
        if (source == null) {
            return source;
        }
        var cyrillicTransformed = cyrillicTranslator.translate(source);
        return Greeklish.toGreeklish(cyrillicTransformed);
    }
}
