// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import static java.time.OffsetDateTime.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.genomicdatainfrastructure.discovery.model.DatasetOrganization;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanOrganization;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import io.github.genomicdatainfrastructure.discovery.utils.PackagesSearchResponseMapper;

class PackagesSearchResponseMapperTest {

    @Test
    void accepts_null_result() {
        var packagesSearchResponse = PackagesSearchResponse.builder()
                .result(null)
                .build();

        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse);
        var expected = DatasetsSearchResponse.builder()
                .results(List.of())
                .facetGroupCount(Map.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()))
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
                packagesSearchResponse);
        var expected = DatasetsSearchResponse.builder()
                .results(List.of())
                .facetGroupCount(Map.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()))
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
                packagesSearchResponse);
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()))
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
                packagesSearchResponse);
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of())
                .facetGroups(List.of(
                        FacetGroup.builder()
                                .key("ckan")
                                .label("DCAT-AP")
                                .facets(List.of())
                                .build()))
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
                packagesSearchResponse);
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
                                .build()))
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
                packagesSearchResponse);
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
                                .build()))
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
                                                        .build()))
                                        .build()))
                        .count(1)
                        .results(List.of(
                                CkanPackage.builder()
                                        .id("id")
                                        .identifier("identifier")
                                        .title("title")
                                        .notes("notes")
                                        .organization(CkanOrganization
                                                .builder()
                                                .title("organization")
                                                .imageUrl("image.com")
                                                .description("desc")
                                                .name("org")
                                                .build())
                                        .theme(List.of(
                                                CkanValueLabel.builder()
                                                        .displayName("theme")
                                                        .name("theme")
                                                        .build()))
                                        .publisherName("publisherName")
                                        .issued("2007-12-03T10:15:30+01:00")
                                        .modified("2024-09-20T00:00:00+00:00")
                                        .build()))
                        .build())
                .build();
        var actual = PackagesSearchResponseMapper.from(
                packagesSearchResponse);
        var expected = DatasetsSearchResponse.builder()
                .count(1)
                .facetGroupCount(Map.of("ckan", 1))
                .results(List.of(
                        SearchedDataset.builder()
                                .id("id")
                                .identifier("identifier")
                                .title("title")
                                .description("notes")
                                .organization(DatasetOrganization
                                        .builder()
                                        .title("organization")
                                        .imageUrl("image.com")
                                        .description("desc")
                                        .name("org")
                                        .build())
                                .themes(List.of(
                                        ValueLabel.builder()
                                                .value("theme")
                                                .label("theme")
                                                .build()))
                                .catalogue("organization")
                                .createdAt(parse("2007-12-03T10:15:30+01:00"))
                                .modifiedAt(parse("2024-09-20T00:00:00+00:00"))
                                .build()))
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
                                                        .build()))
                                        .build()))
                                .build()))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
