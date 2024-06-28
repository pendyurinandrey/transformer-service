package org.transformerservice.services;

import org.springframework.stereotype.Service;
import org.transformerservice.annotations.TransformerSpec;
import org.transformerservice.dto.ElementDTO;
import org.transformerservice.dto.TransformedElementDTO;
import org.transformerservice.exceptions.InvalidTransformerConfigurationException;
import org.transformerservice.exceptions.UnknownTransformerException;
import org.transformerservice.transformers.Transformer;

import java.util.*;

@Service
public class TransformationService {

    private final Map<String, Transformer> idToTransformer;

    public TransformationService(List<Transformer> transformers) {
        this.idToTransformer = prepareTransformerMap(transformers);
    }

    public List<TransformedElementDTO> transform(List<ElementDTO> elements) {
        if (elements == null) {
            return Collections.emptyList();
        }
        var result = new ArrayList<TransformedElementDTO>(elements.size());
        for (var el : elements) {
            var currentValue = el.getValue();
            for (var transformerCfg : el.getTransformers()) {
                var id = computeId(transformerCfg.getGroupId(), transformerCfg.getTransformerId());
                if (!idToTransformer.containsKey(id)) {
                    var msg = String.format("Transform groupId = %s, transformerId = %s is unknown",
                            transformerCfg.getGroupId(), transformerCfg.getTransformerId());
                    throw new UnknownTransformerException(msg);
                }
                currentValue = idToTransformer.get(id).transform(currentValue, transformerCfg.getProperties());
            }
            result.add(new TransformedElementDTO(el.getValue(), currentValue));
        }
        return result;
    }

    private Map<String, Transformer> prepareTransformerMap(List<Transformer> transformers) {
        var result = new HashMap<String, Transformer>();
        for (var tr : transformers) {
            var annotation = tr.getClass().getDeclaredAnnotation(TransformerSpec.class);
            if (annotation == null) {
                /*
                    If an annotation isn't set to a class that implements the 'Transformer' interface,
                    it won't be possible to use this class in the REST API (groupId and transformerId will be unknown).
                    It's probably better to signal to a contributor that something went wrong during
                    the application's start, rather than skip this transformer.
                 */
                var msg = String.format("Transformer %s is not annotated with @TransformerSpec",
                        tr.getClass().getCanonicalName());
                throw new InvalidTransformerConfigurationException(msg);
            } else {
                var id = computeId(annotation.groupId(), annotation.transformerId());
                if (result.containsKey(id)) {
                    /*
                        Probably, it's better to fail the Spring Context here rather than try
                        to somehow resolve the duplicated groupId:transformerId.
                     */
                    var msg = String.format("More then one transformer has identical groupId:transformerId: %s",
                            id);
                    throw new InvalidTransformerConfigurationException(msg);
                }
                result.put(id, tr);
            }
        }
        return Collections.unmodifiableMap(result);
    }

    private String computeId(String groupId, String transformerId) {
        return String.format("%s:%s", groupId, transformerId);
    }
}
