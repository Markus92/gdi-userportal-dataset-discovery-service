// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.services.DatasetsSearchService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatasetQueryApiImpl implements DatasetQueryApi {

    private final SecurityIdentity identity;
    private final DatasetsSearchService datasetsSearchService;

    @Override
    public DatasetsSearchResponse datasetSearch(DatasetSearchQuery datasetSearchQuery) {
        return datasetsSearchService.search(accessToken(), datasetSearchQuery);
    }

    @Override
    public RetrievedDataset retrieveDataset(String id) {
        return RetrievedDataset.builder()
                .build();
    }

    private String accessToken() {
        if (identity.isAnonymous()) {
            return null;
        }
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getRawToken();
    }
}
