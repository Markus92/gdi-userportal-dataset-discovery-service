// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.repositories;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.services.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.services.DatasetsSearchService;
import io.github.genomicdatainfrastructure.discovery.services.PackagesSearchResponseMapper;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@LookupIfProperty(name = "repositories.gdi", stringValue = "true")
@ApplicationScoped
public class GdiDatasetsRepository implements DatasetsRepository {

    private static final String SELECTED_FACETS = "[\"access_rights\",\"theme\",\"tags\",\"spatial_uri\",\"organization\",\"publisher_name\",\"res_format\"]";
    private final CkanQueryApi ckanQueryApi;

    @Inject
    public GdiDatasetsRepository(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public PackagesSearchResponse search(DatasetSearchQuery query, String accessToken) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query);

        return ckanQueryApi.packageSearch(
                query.getQuery(),
                facetsQuery,
                query.getSort(),
                query.getRows(),
                query.getStart(),
                SELECTED_FACETS,
                accessToken
        );
    }

    @Override
    public CkanPackageShowResponse retrieveCkanPackage(String id, String accessToken) {
        return ckanQueryApi.packageShow(id, accessToken);
    }
}
