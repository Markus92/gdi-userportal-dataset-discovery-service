// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.services.RetrieveDatasetService;
import io.github.genomicdatainfrastructure.discovery.services.SearchDatasetsService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatasetQueryApiImpl implements DatasetQueryApi {

    private final SecurityIdentity identity;
    private final SearchDatasetsService searchDatasetsService;
    private final RetrieveDatasetService retrievedDatasetService;

    @Override
    public Response datasetSearch(DatasetSearchQuery datasetSearchQuery) {
        var content = searchDatasetsService.search(datasetSearchQuery, accessToken());
        return Response.ok(content).build();
    }

    @Override
    public Response retrieveDataset(String id) {
        var content = retrievedDatasetService.retrieve(id, accessToken());
        return Response.ok(content).build();
    }

    @Override
    public Response retrieveDatasetInFormat(String id, String format) {
        var type = getType(format);
        var content = retrievedDatasetService.retrieveInFormat(id, format, accessToken());
        return Response
                .ok(content)
                .type(type)
                .build();
    }

    private String getType(String format) {
        return switch (format) {
            case "jsonld" -> "application/ld+json";
            case "rdf" -> "application/rdf+xml";
            case "ttl" -> "text/turtle";
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    private String accessToken() {
        if (identity.isAnonymous()) {
            return null;
        }
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getRawToken();
    }
}
