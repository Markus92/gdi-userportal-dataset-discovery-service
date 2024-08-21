package io.github.genomicdatainfrastructure.discovery.datasets.infra.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.utils.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.utils.PackagesSearchResponseMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Set;
import java.util.stream.Collectors;

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
    public Set<String> collect(DatasetSearchQuery query, String accessToken) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query);

        var response = ckanQueryApi.packageSearch(
                query.getQuery(),
                facetsQuery,
                query.getSort(),
                query.getRows(),
                query.getStart(),
                null,
                accessToken
        );

        DatasetsSearchResponse searchResponse = PackagesSearchResponseMapper.from(response);

        var datasetIds = searchResponse
                .getResults()
                .stream()
                .map(SearchedDataset::getId)
                .collect(Collectors.toSet());

        return datasetIds;
    }
}
