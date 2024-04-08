// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import io.github.genomicdatainfrastructure.discovery.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.java.Log;
import java.util.logging.Level;

@Log
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        log.log(Level.SEVERE, exception, exception::getMessage);

        var errorResponse = ErrorResponse.builder()
                .title("Not expected exception")
                .status(INTERNAL_SERVER_ERROR.getStatusCode())
                .detail(exception.getMessage())
                .build();

        return Response
                .status(INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
