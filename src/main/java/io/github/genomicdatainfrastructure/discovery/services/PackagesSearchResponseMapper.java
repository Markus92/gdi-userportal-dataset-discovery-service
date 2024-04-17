// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;

@UtilityClass
public class PackagesSearchResponseMapper {

    public static final String CKAN_FACET_GROUP = "ckan";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    public DatasetsSearchResponse from(PackagesSearchResponse response) {
        var count = count(response.getResult());
        var facetGroupCount = Map.<String, Integer>of();
        if (count != null) {
            facetGroupCount = Map.of(CKAN_FACET_GROUP, count);
        }

        return DatasetsSearchResponse.builder()
                .count(count)
                .facetGroups(facetGroups(response.getResult()))
                .results(results(response.getResult()))
                .facetGroupCount(facetGroupCount)
                .build();
    }

    private Integer count(PackagesSearchResult result) {
        return ofNullable(result)
                .map(PackagesSearchResult::getCount)
                .orElse(null);
    }

    private List<FacetGroup> facetGroups(PackagesSearchResult result) {
        var nonNullSearchFacets = ofNullable(result)
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of);

        return List.of(facetGroup(nonNullSearchFacets));
    }

    private FacetGroup facetGroup(Map<String, CkanFacet> facets) {
        return FacetGroup.builder()
                .key(CKAN_FACET_GROUP)
                .label("DCAT-AP")
                .facets(facets.entrySet().stream()
                        .map(PackagesSearchResponseMapper::facet)
                        .toList())
                .build();
    }

    private Facet facet(Map.Entry<String, CkanFacet> entry) {
        var key = entry.getKey();
        var facet = entry.getValue();
        var values = ofNullable(facet.getItems())
                .orElseGet(List::of)
                .stream()
                .map(value -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .build()
                )
                .toList();

        return Facet.builder()
                .key(key)
                .label(facet.getTitle())
                .values(values)
                .build();
    }

    private List<SearchedDataset> results(PackagesSearchResult result) {
        var nonNullPackages = ofNullable(result)
                .map(PackagesSearchResult::getResults)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        return nonNullPackages.stream()
                .map(PackagesSearchResponseMapper::result)
                .toList();
    }

    private SearchedDataset result(CkanPackage dataset) {
        var catalogue = ofNullable(dataset.getOrganization())
                .map(CkanOrganization::getTitle)
                .orElse(null);

        return SearchedDataset.builder()
                .id(dataset.getId())
                .identifier(dataset.getIdentifier())
                .title(dataset.getName())
                .description(dataset.getNotes())
                .themes(values(dataset.getTheme()))
                .catalogue(catalogue)
                .modifiedAt(parse(dataset.getMetadataModified()))
                .createdAt(parse(dataset.getMetadataCreated()))
                .build();
    }

    private LocalDateTime parse(String date) {
        return ofNullable(date)
                .map(it -> LocalDateTime.parse(it, DATE_FORMATTER))
                .orElse(null);
    }

    private List<ValueLabel> values(List<CkanValueLabel> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(PackagesSearchResponseMapper::value)
                .filter(Objects::nonNull)
                .toList();
    }

    private ValueLabel value(CkanValueLabel value) {
        return ofNullable(value)
                .filter(Objects::nonNull)
                .map(it -> ValueLabel.builder()
                        .value(it.getName())
                        .label(it.getDisplayName())
                        .build())
                .orElse(null);
    }
}
