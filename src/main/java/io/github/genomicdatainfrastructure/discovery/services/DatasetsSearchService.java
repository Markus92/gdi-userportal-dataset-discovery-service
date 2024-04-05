// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DatasetsSearchService {

    private static final String FACETS_QUERY = "[\"access_rights\",\"theme\",\"tags\",\"spatial_uri\",\"organization\",\"publisher_name\",\"res_format\"]";

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public DatasetsSearchService(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    public DatasetsSearchResponse search(String accessToken, DatasetSearchQuery query) {
        var response = ckanQueryApi.packageSearch(
                query.getQuery(),
                CkanFacetsQueryBuilder.buildFacetQuery(query.getFacets()),
                query.getSort(),
                query.getRows(),
                query.getStart(),
                FACETS_QUERY,
                accessToken
        );
        return PackagesSearchResponseMapper.from(response);
    }
}
