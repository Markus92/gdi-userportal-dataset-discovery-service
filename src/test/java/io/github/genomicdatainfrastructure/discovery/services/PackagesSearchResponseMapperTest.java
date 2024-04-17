// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import static java.time.LocalDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import org.junit.jupiter.api.Test;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;

import java.time.format.DateTimeFormatter;

class PackagesSearchResponseMapperTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    @Test
    void accepts_null_result() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(null)
                .build();

        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse
        );
        var expected = DatasetsSearchResponse.builder()
                .results(List.of())
                .facetGroupCount(Map.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void accepts_empty_result() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(null)
                        .count(null)
                        .results(null)
                        .build())
                .build();

        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse
        );
        var expected = DatasetsSearchResponse.builder()
                .results(List.of())
                .facetGroupCount(Map.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void accepts_empty_result_results() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(null)
                        .count(1)
                        .results(List.of())
                        .build())
                .build();
        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse
        );
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void accepts_empty_result_search_facets() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(Map.of())
                        .count(1)
                        .results(List.of())
                        .build())
                .build();
        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse
        );
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void accepts_null_result_search_facets_values() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(Map.of("dummy", CkanFacet.builder()
                                .items(null)
                                .build()))
                        .count(1)
                        .results(List.of())
                        .build())
                .build();
        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse
        );
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of(Facet.builder()
                                        .key("dummy")
                                        .label(null)
                                        .values(List.of())
                                        .build()))
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void accepts_empty_result_search_facets_values() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(Map.of("dummy", CkanFacet.builder()
                                .items(List.of())
                                .build()))
                        .count(1)
                        .results(List.of())
                        .build())
                .build();
        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse
        );
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of(Facet.builder()
                                        .key("dummy")
                                        .label(null)
                                        .values(List.of())
                                        .build()))
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void can_parse() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(Map.of(
                                "dummy",
                                CkanFacet.builder()
                                        .title("dummy label")
                                        .items(List.of(
                                                CkanValueLabel.builder()
                                                        .name("value")
                                                        .displayName("label")
                                                        .build()
                                        ))
                                        .build()))
                        .count(1)
                        .results(List.of(
                                CkanPackage.builder()
                                        .id("id")
                                        .identifier("identifier")
                                        .name("name")
                                        .notes("notes")
                                        .theme(List.of(
                                                CkanValueLabel.builder()
                                                        .displayName("theme")
                                                        .name("theme")
                                                        .build()))
                                        .publisherName("publisherName")
                                        .organization(CkanOrganization.builder()
                                                .title("title")
                                                .build())
                                        .metadataModified("2024-03-19T13:37:05.472970")
                                        .build()
                        ))
                        .build())
                .build();
        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse
        );
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of(
                        SearchedDataset.builder()
                                .id("id")
                                .identifier("identifier")
                                .title("name")
                                .description("notes")
                                .themes(List.of(
                                        ValueLabel.builder()
                                                .value("theme")
                                                .label("theme")
                                                .build()
                                ))
                                .catalogue("title")
                                .modifiedAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                                .build()
                ))
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of(Facet.builder()
                                        .key("dummy")
                                        .label("dummy label")
                                        .values(List.of(
                                                ValueLabel.builder()
                                                        .value("value")
                                                        .label("label")
                                                        .build()
                                        ))
                                        .build()))
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
