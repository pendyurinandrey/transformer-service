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
                    If annotation doesn't set to a class which implements 'Transformer' interface, then
                    it'll not be possible to use this class in REST API (groupId and transformerId is unknown).
                    Probably, it's better to signal a contributor that something went wrong during
                    the applications start, rather than skip this transformer.
                 */
                var msg = String.format("Transformer %s does not annotated with @TransformerSpec",
                        tr.getClass().getCanonicalName());
                throw new InvalidTransformerConfigurationException(msg);
            } else {
                var id = computeId(annotation.groupId(), annotation.transformerId());
                if (result.containsKey(id)) {
                    /*
                        Probably, it's better to fail Spring Context here rather than
                        trying to somehow resolve duplicated groupId:transformerId.
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
