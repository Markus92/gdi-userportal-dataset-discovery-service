package io.github.genomicdatainfrastructure.discovery.datasets.infra.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.utils.PackagesSearchResponseMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Set;
import java.util.stream.Collectors;

import static io.github.genomicdatainfrastructure.discovery.utils.PackagesSearchResponseMapper.CKAN_FACET_GROUP;

@ApplicationScoped
public class CkanDatasetsRepository implements DatasetsRepository {

    private static final String CKAN_IDENTIFIER_FIELD = "identifier";

    private final CkanQueryApi ckanQueryApi;
    private final CkanDatasetIdsCollector ckanSearchContextBuilder;

    @Inject
    public CkanDatasetsRepository(
            @RestClient CkanQueryApi ckanQueryApi,
            CkanDatasetIdsCollector ckanSearchContextBuilder
    ) {
        this.ckanQueryApi = ckanQueryApi;
        this.ckanSearchContextBuilder = ckanSearchContextBuilder;
    }

    @Override
    public Set<SearchedDataset> search(Set<String> datasetIds, String accessToken) {

        var facetsQuery = DatasetSearchQueryFacet.builder()
                .facetGroup(CKAN_FACET_GROUP)
                .facet(CKAN_IDENTIFIER_FIELD)
                .value(datasetIds.stream().reduce((a, b) -> a + " OR " + b).orElse(""))
                .build()
                .toString();

        var response = ckanQueryApi.packageSearch(
                "",
                facetsQuery,
                null,
                null,
                null,
                null,
                accessToken
        );

        DatasetsSearchResponse searchResponse = PackagesSearchResponseMapper.from(response);

        return searchResponse
                .getResults()
                .stream()
                .collect(Collectors.toSet());
    }
}
