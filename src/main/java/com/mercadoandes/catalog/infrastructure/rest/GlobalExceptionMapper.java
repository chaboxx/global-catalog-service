package com.mercadoandes.catalog.infrastructure.rest;

import com.mercadoandes.catalog.application.exception.InvalidProductIdentityException;
import com.mercadoandes.catalog.application.exception.ProductAlreadyExistsException;
import com.mercadoandes.catalog.application.exception.ProductNotFoundException;
import com.mercadoandes.catalog.infrastructure.rest.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.UUID;
import java.util.stream.Collectors;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        String traceId = UUID.randomUUID().toString();

        if (exception instanceof ConstraintViolationException validationException) {
            String message = validationException.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .collect(Collectors.joining("; "));
            return buildResponse(Response.Status.BAD_REQUEST, "VALIDATION_ERROR", message, traceId);
        }

        if (exception instanceof InvalidProductIdentityException) {
            return buildResponse(Response.Status.BAD_REQUEST, "BAD_REQUEST", exception.getMessage(), traceId);
        }

        if (exception instanceof ProductAlreadyExistsException) {
            return buildResponse(Response.Status.CONFLICT, "CONFLICT", exception.getMessage(), traceId);
        }

        if (exception instanceof ProductNotFoundException) {
            return buildResponse(Response.Status.NOT_FOUND, "NOT_FOUND", exception.getMessage(), traceId);
        }

        if (exception instanceof WebApplicationException webException) {
            Response.StatusType statusInfo = webException.getResponse().getStatusInfo();
            String code = statusInfo.getReasonPhrase().toUpperCase().replace(' ', '_');
            String message = webException.getMessage() == null || webException.getMessage().isBlank()
                    ? statusInfo.getReasonPhrase()
                    : webException.getMessage();
            return buildResponse(statusInfo, code, message, traceId);
        }

        return buildResponse(
                Response.Status.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Unexpected error",
                traceId);
    }

    private Response buildResponse(Response.StatusType status, String code, String message, String traceId) {
        return Response.status(status)
                .entity(new ErrorResponse(code, message, traceId))
                .build();
    }
}
