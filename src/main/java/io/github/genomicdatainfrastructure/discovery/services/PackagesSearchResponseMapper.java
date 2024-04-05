// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanOrganization;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import java.util.List;
import java.util.Map;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import static java.util.Optional.ofNullable;

public class PackagesSearchResponseMapper {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    private PackagesSearchResponseMapper() {
        // Utility class
    }

    public static DatasetsSearchResponse from(PackagesSearchResponse response) {
        return DatasetsSearchResponse.builder()
                .count(count(response.getResult()))
                .facetGroups(facetGroups(response.getResult()))
                .results(results(response.getResult()))
                .build();
    }

    private static Integer count(PackagesSearchResult result) {
        return ofNullable(result)
                .map(PackagesSearchResult::getCount)
                .orElse(null);
    }

    private static List<FacetGroup> facetGroups(PackagesSearchResult result) {
        var nonNullSearchFacets = ofNullable(result)
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of);

        return List.of(facetGroup(nonNullSearchFacets));
    }

    private static FacetGroup facetGroup(Map<String, CkanFacet> facets) {
        return FacetGroup.builder()
                .key("ckan")
                .label("Metadata")
                .facets(facets.entrySet().stream()
                        .map(PackagesSearchResponseMapper::facet)
                        .toList())
                .build();
    }

    private static Facet facet(Map.Entry<String, CkanFacet> entry) {
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

    private static List<SearchedDataset> results(PackagesSearchResult result) {
        var nonNullPackages = ofNullable(result)
                .map(PackagesSearchResult::getResults)
                .orElseGet(List::of);

        return nonNullPackages.stream()
                .map(PackagesSearchResponseMapper::result)
                .toList();
    }

    private static SearchedDataset result(CkanPackage dataset) {
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
                .build();
    }

    private static LocalDateTime parse(String date) {
        return ofNullable(date)
                .map(it -> LocalDateTime.parse(it, DATE_FORMATTER))
                .orElse(null);
    }

    private static List<ValueLabel> values(List<String> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(it -> ValueLabel.builder()
                        .value(it)
                        .label(it)
                        .build())
                .toList();
    }
}
