package org.transformerservice.errorhandlers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.transformerservice.dto.DefaultErrorResponseDTO;
import org.transformerservice.exceptions.InvalidTransformerConfigurationException;
import org.transformerservice.exceptions.UnknownTransformerException;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UnknownTransformerException.class, InvalidTransformerConfigurationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<DefaultErrorResponseDTO> handleBadRequest(RuntimeException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new DefaultErrorResponseDTO(ex.getMessage()));
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            HttpServletResponse response = servletWebRequest.getResponse();
            if (response != null && response.isCommitted()) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Response already committed. Ignoring: " + ex);
                }

                return null;
            }
        }

        var newBody = new DefaultErrorResponseDTO(ex.getMessage());
        return createResponseEntity(newBody, headers, statusCode, request);
    }
}
