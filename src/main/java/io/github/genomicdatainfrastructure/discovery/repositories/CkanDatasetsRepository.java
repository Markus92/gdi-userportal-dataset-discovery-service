// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.repositories;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.utils.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.utils.PackagesSearchResponseMapper;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@LookupIfProperty(name = "sources.ckan", stringValue = "true")
@ApplicationScoped
public class CkanDatasetsRepository implements DatasetsRepository {

    private static final String SELECTED_FACETS = "[\"access_rights\",\"theme\",\"tags\",\"spatial_uri\",\"organization\",\"publisher_name\",\"res_format\"]";
    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanDatasetsRepository(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public DatasetsSearchResponse search(DatasetSearchQuery query, String accessToken) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query);

        String sort_string = query.getSort();
        if (sort_string != null) {
            if (sort_string.contains("title") && !sort_string.contains("title_string")) {
                sort_string = sort_string.replace("title", "title_string");
            }
        }

        var response = ckanQueryApi.packageSearch(
                query.getQuery(),
                facetsQuery,
                sort_string,
                query.getRows(),
                query.getStart(),
                SELECTED_FACETS,
                accessToken
        );

        return PackagesSearchResponseMapper.from(response);
    }
}
