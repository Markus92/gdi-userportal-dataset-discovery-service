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
public class CkanDatasetsSearchService implements DatasetsSearchService {

    private static final String SELECTED_FACETS = "[\"access_rights\",\"theme\",\"tags\",\"spatial_uri\",\"organization\",\"publisher_name\",\"res_format\"]";

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanDatasetsSearchService(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public DatasetsSearchResponse search(DatasetSearchQuery query, String accessToken) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query.getFacets());

        var response = ckanQueryApi.packageSearch(
                query.getQuery(),
                facetsQuery,
                query.getSort(),
                query.getRows(),
                query.getStart(),
                SELECTED_FACETS,
                accessToken
        );

        return PackagesSearchResponseMapper.from(response);
    }
}
