// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.applications.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SearchDatasetsQuery {

    private final DatasetsRepository repository;
    private final Instance<DatasetIdsCollector> collectors;
    private final Instance<FacetsBuilder> facetsBuilders;

    public DatasetsSearchResponse execute(DatasetSearchQuery query, String accessToken) {
        var datasetIds = collectors
                .stream()
                .map(collector -> collector.collect(query, accessToken))
                .filter(Objects::nonNull)
                .reduce(this::findIdsIntersection)
                .orElse(List.of());

        var datasets = repository.search(datasetIds,
                query.getSort(),
                query.getRows(),
                query.getStart(),
                accessToken);

        var facetGroups = facetsBuilders
                .stream()
                .map(facetBuilder -> facetBuilder.build(query, accessToken))
                .filter(Objects::nonNull)
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
