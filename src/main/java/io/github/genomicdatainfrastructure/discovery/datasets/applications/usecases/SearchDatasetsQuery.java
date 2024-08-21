package io.github.genomicdatainfrastructure.discovery.datasets.applications.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import jakarta.inject.Inject;

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
                .reduce((a, b) -> findIdsIntersection(a, b))
                .orElse(Set.of());

        var datasets = repository.search(datasetIds, this.accessToken);

        var facets = facetsBuilders
                .stream()
                .map(builder -> builder.buildFacets(this.query))
                .reduce((a, b) -> a.addAll(b) ? a : b)
                .orElse(Set.of());

        return DatasetsSearchResponse
                .builder()
                .count(datasets.size())
                .results(datasets.stream().toList())
                .facetGroups(facets)
                .facetGroupCount(facets.size())
                .build();
    }

    private Set<String> findIdsIntersection(Set<String> a, Set<String> b) {
        return a.stream()
                .filter(b::contains)
                .collect(Collectors.toSet());
    }
}
