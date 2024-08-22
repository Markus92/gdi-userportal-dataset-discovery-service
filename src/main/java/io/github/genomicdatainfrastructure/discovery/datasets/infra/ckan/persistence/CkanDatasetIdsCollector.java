// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infra.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.utils.CkanFacetsQueryBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.datasets.infra.ckan.config.CkanConfiguration.CKAN_IDENTIFIER_FIELD;

@ApplicationScoped
public class CkanDatasetIdsCollector implements DatasetIdsCollector {

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanDatasetIdsCollector(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public List<String> collect(DatasetSearchQuery query, String accessToken) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query);

        var response = ckanQueryApi.packageSearch(
                query.getQuery(),
                facetsQuery,
                CKAN_IDENTIFIER_FIELD,
                "",
                1000,
                0,
                "",
                accessToken
        );

        var datasetIds = response
                .getResult()
                .getResults()
                .stream()
                .map(CkanPackage::getIdentifier)
                .toList();

        return datasetIds;

    }
}
