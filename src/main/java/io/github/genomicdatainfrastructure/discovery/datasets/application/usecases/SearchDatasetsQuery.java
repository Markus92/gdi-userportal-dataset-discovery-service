// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.RecordsCountCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SearchDatasetsQuery {

    private final DatasetsRepository repository;
    private final Instance<DatasetIdsCollector> collectors;
    private final Instance<FacetsBuilder> facetsBuilders;
    private final RecordsCountCollector recordsCountCollector;

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

        var potentialRecordsCounts = ofNullable(recordsCountCollector.collectRecordsCount(query,
                accessToken));

        var enhancedDatasets = datasets
                .stream()
                .map(dataset -> dataset
                        .toBuilder()
                        .recordsCount(potentialRecordsCounts
                                .map(recordsCounts -> recordsCounts.get(dataset.getIdentifier()))
                                .orElse(null))
                        .build())
                .toList();

        var facetGroups = facetsBuilders
                .stream()
                .map(facetBuilder -> facetBuilder.build(query, accessToken))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return DatasetsSearchResponse
                .builder()
                .count(datasetIds.size())
                .results(enhancedDatasets)
                .facetGroups(facetGroups)
                .build();
    }

    private List<String> findIdsIntersection(List<String> a, List<String> b) {
        return a.stream()
                .filter(b::contains)
                .toList();
    }
}
