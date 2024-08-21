package io.github.genomicdatainfrastructure.discovery.datasets.applications.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchDatasetsQuery {
    private final DatasetSearchQuery query;
    private final String accessToken;
    private final DatasetsRepository repository;
    private final Set<DatasetIdsCollector> collectors;
    private final Set<FacetsBuilder> facetsBuilders;

    @Inject
    public SearchDatasetsQuery(DatasetSearchQuery query,
                               String accessToken,
                               DatasetsRepository repository,
                               Set<DatasetIdsCollector> collectors,
                               Set<FacetsBuilder> facetsBuilders) {
        this.query = query;
        this.accessToken = accessToken;
        this.repository = repository;
        this.collectors = collectors;
        this.facetsBuilders = facetsBuilders;
    }

    public DatasetsSearchResponse execute() {
        var datasetIds = collectors
                .stream()
                .map(collector -> collector.collect(this.query, this.accessToken))
                .filter(Objects::nonNull)
                .reduce((a, b) -> findIdsIntersection(a, b))
                .orElse(List.of());

        var datasets = repository.search(datasetIds,
                                            query.getSort(),
                                            query.getRows(),
                                            query.getStart(),
                                            this.accessToken);

        var facetGroups = facetsBuilders
                .stream()
                .map(facetBuilder -> facetBuilder.buildFacets(query, accessToken))
                .collect(Collectors.toList());

        return DatasetsSearchResponse
                .builder()
                .count(datasetIds.size())
                .results(datasets)
                .facetGroups(facetGroups)
                .build();
    }

    private List<String> findIdsIntersection(List<String> a, List<String> b) {
        return a.stream()
                .filter(b::contains)
                .toList();
    }
}
