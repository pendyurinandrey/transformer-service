package org.transformerservice.controllers;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.transformerservice.dto.TransformRequestDTO;
import org.transformerservice.dto.TransformResponseDTO;
import org.transformerservice.services.TransformationService;


@RestController
@RequestMapping("/api/v1/transform")
public class TransformationController {

    private final TransformationService transformationService;

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransformResponseDTO> transform(@Valid @RequestBody TransformRequestDTO req) {
        return ResponseEntity.ok(new TransformResponseDTO(transformationService.transform(req.getElements())));
    }
}
