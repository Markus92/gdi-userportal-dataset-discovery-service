// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import io.github.genomicdatainfrastructure.discovery.repositories.BeaconDatasetsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.genomicdatainfrastructure.discovery.remote.beacon.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResultSet;
import io.github.genomicdatainfrastructure.discovery.remote.keycloak.api.KeycloakQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.keycloak.model.KeycloakTokenResponse;
import jakarta.ws.rs.WebApplicationException;

import javax.xml.crypto.Data;

class BeaconDatasetsRepositoryTest {

    private BeaconDatasetsRepository underTest;
    private BeaconQueryApi beaconQueryApi;
    private KeycloakQueryApi keycloakQueryApi;
    private BeaconFilteringTermsService beaconFilteringTermsService;
    private CkanQueryApi ckanQueryApi;

    @BeforeEach
    void setUp() {
        beaconQueryApi = mock(BeaconQueryApi.class);
        keycloakQueryApi = mock(KeycloakQueryApi.class);
        ckanQueryApi = mock(CkanQueryApi.class);
        beaconFilteringTermsService = mock(BeaconFilteringTermsService.class);

        underTest = new BeaconDatasetsRepository(
                ckanQueryApi,
                beaconQueryApi,
                "beaconIdpAlias",
                keycloakQueryApi,
                beaconFilteringTermsService
        );
    }

    @Test
    void doesnt_call_beacon_if_access_token_is_null() {
        when(ckanQueryApi.packageSearch(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder()
                                .count(1)
                                .results(List.of(CkanPackage.builder()
                                        .id("id")
                                        .title("title")
                                        .build())
                                )
                                .build())

                        .build());

        var query = DatasetSearchQuery.builder()
                .build();
        var actual = underTest.search(query, null);

        verify(keycloakQueryApi, never()).retriveIdpTokens(any(), any());
        verify(beaconFilteringTermsService, never()).listFilteringTerms(any());
        verify(beaconQueryApi, never()).listIndividuals(any(), any());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(DatasetsSearchResponse.builder()
                        .count(1)
                        .facetGroupCount(Map.of("ckan", 1))
                        .facetGroups(List.of(FacetGroup.builder().key("ckan").label("DCAT-AP")
                                .facets(List.of()).build()))
                        .results(List.of(
                                SearchedDataset.builder()
                                        .id("id")
                                        .title("title")
                                        .themes(List.of())
                                        .build()
                        ))
                        .build());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403})
    void doesnt_call_beacon_if_keycloak_throws_expected_4xx_errors(Integer statusCode) {
        when(ckanQueryApi.packageSearch(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder()
                                .count(1)
                                .results(List.of(CkanPackage.builder()
                                        .id("id")
                                        .title("title")
                                        .build())
                                )
                                .build())

                        .build());

        when(keycloakQueryApi.retriveIdpTokens("beaconIdpAlias", "Bearer dummy"))
                .thenThrow(new WebApplicationException(statusCode));

        var query = DatasetSearchQuery.builder()
                .build();
        var actual = underTest.search(query, "dummy");

        verify(keycloakQueryApi).retriveIdpTokens(any(), any());
        verify(ckanQueryApi).packageSearch(any(), any(), any(), any(), any(), any(), any());
        verify(beaconFilteringTermsService, never()).listFilteringTerms(any());
        verify(beaconQueryApi, never()).listIndividuals(any(), any());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(DatasetsSearchResponse.builder()
                        .count(1)
                        .facetGroupCount(Map.of("ckan", 1))
                        .facetGroups(List.of(FacetGroup.builder().key("ckan").label("DCAT-AP")
                                .facets(List.of()).build()))
                        .results(List.of(
                                SearchedDataset.builder()
                                        .id("id")
                                        .title("title")
                                        .themes(List.of())
                                        .build()
                        ))
                        .build());
    }

    @Test
    void doesnt_call_beacon_if_there_are_no_beacon_filters() {
        when(ckanQueryApi.packageSearch(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder()
                                .count(1)
                                .results(List.of(CkanPackage.builder()
                                        .id("id")
                                        .title("title")
                                        .build())
                                )
                                .build())
                        .build());

        when(beaconFilteringTermsService.listFilteringTerms(any()))
                .thenReturn(FacetGroup.builder()
                        .key("beacon")
                        .label("label")
                        .facets(List.of(
                                Facet.builder()
                                        .key("key")
                                        .label("label")
                                        .build()
                        ))
                        .build());

        when(keycloakQueryApi.retriveIdpTokens("beaconIdpAlias", "Bearer dummy"))
                .thenReturn(KeycloakTokenResponse.builder()
                        .accessToken("beaconAccessToken")
                        .build());

        var query = DatasetSearchQuery.builder()
                .build();
        var actual = underTest.search(query, "dummy");

        verify(keycloakQueryApi).retriveIdpTokens(any(), any());
        verify(beaconQueryApi, never()).listIndividuals(any(), any());
        verify(ckanQueryApi).packageSearch(any(), any(), any(), any(), any(), any(), any());
        verify(beaconFilteringTermsService).listFilteringTerms(any());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(DatasetsSearchResponse.builder()
                        .count(1)
                        .facetGroupCount(Map.of(
                                "ckan", 1,
                                "beacon", 0
                        ))
                        .results(List.of(
                                SearchedDataset.builder()
                                        .id("id")
                                        .title("title")
                                        .themes(List.of())
                                        .build()
                        ))
                        .facetGroups(List.of(FacetGroup.builder().key("ckan").label("DCAT-AP")
                                .facets(List.of()).build()))
                        .facetGroups(List.of(
                                FacetGroup.builder()
                                        .key("beacon")
                                        .label("label")
                                        .facets(List.of(
                                                Facet.builder()
                                                        .key("key")
                                                        .label("label")
                                                        .build()
                                        ))
                                        .build(),
                                FacetGroup.builder()
                                        .key("ckan")
                                        .label("DCAT-AP")
                                        .facets(List.of())
                                        .build()
                        ))
                        .build());
    }

    private static Stream<BeaconIndividualsResponse> emptyBeaconResultsets() {
        return Stream.of(
                BeaconIndividualsResponse.builder()
                        .response(null)
                        .build(),
                BeaconIndividualsResponse.builder()
                        .response(BeaconIndividualsResponseContent.builder()
                                .resultSets(List.of())
                                .build())
                        .build(),
                BeaconIndividualsResponse.builder()
                        .response(BeaconIndividualsResponseContent.builder()
                                .resultSets(List.of(
                                        BeaconResultSet.builder()
                                                .id(null)
                                                .resultsCount(1)
                                                .setType("dataset")
                                                .build()
                                ))
                                .build())
                        .build(),
                BeaconIndividualsResponse.builder()
                        .response(BeaconIndividualsResponseContent.builder()
                                .resultSets(List.of(
                                        BeaconResultSet.builder()
                                                .id("id")
                                                .resultsCount(null)
                                                .setType("dataset")
                                                .build()
                                ))
                                .build())
                        .build(),
                BeaconIndividualsResponse.builder()
                        .response(BeaconIndividualsResponseContent.builder()
                                .resultSets(List.of(
                                        BeaconResultSet.builder()
                                                .id("id")
                                                .resultsCount(1)
                                                .setType(null)
                                                .build()
                                ))
                                .build())
                        .build()
        );
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("emptyBeaconResultsets")
    void doesnt_call_ckan_if_there_are_no_beacon_resultsets(
            BeaconIndividualsResponse beaconResponse
    ) {
        when(beaconQueryApi.listIndividuals(any(), any()))
                .thenReturn(beaconResponse);

        when(beaconFilteringTermsService.listFilteringTerms(any()))
                .thenReturn(FacetGroup.builder()
                        .key("beacon")
                        .label("label")
                        .facets(List.of(
                                Facet.builder()
                                        .key("key")
                                        .label("label")
                                        .build()
                        ))
                        .build());

        when(keycloakQueryApi.retriveIdpTokens("beaconIdpAlias", "Bearer dummy"))
                .thenReturn(KeycloakTokenResponse.builder()
                        .accessToken("beaconAccessToken")
                        .build());

        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .facetGroup("beacon")
                                .facet("key")
                                .value("value")
                                .build(
                                )))
                .build();
        var actual = underTest.search(query, "dummy");

        verify(keycloakQueryApi).retriveIdpTokens(any(), any());
        verify(beaconQueryApi).listIndividuals(any(), any());
        verify(beaconFilteringTermsService).listFilteringTerms(any());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(DatasetsSearchResponse.builder()
                        .count(0)
                        .facetGroupCount(Map.of(
                                "beacon", 0
                        ))
                        .results(List.of())
                        .facetGroups(List.of(
                                FacetGroup.builder()
                                        .key("beacon")
                                        .label("label")
                                        .facets(List.of(
                                                Facet.builder()
                                                        .key("key")
                                                        .label("label")
                                                        .build()
                                        ))
                                        .build()
                        ))
                        .build());
    }

    @Test
    void calls_ckan_and_beacon() {
        when(beaconQueryApi.listIndividuals(any(), any()))
                .thenReturn(BeaconIndividualsResponse.builder()
                        .response(BeaconIndividualsResponseContent.builder()
                                .resultSets(List.of(
                                        BeaconResultSet.builder()
                                                .id("id")
                                                .resultsCount(1)
                                                .setType("dataset")
                                                .build()
                                ))
                                .build())
                        .build());

        when(ckanQueryApi.packageSearch(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder()
                                .count(1)
                                .results(List.of(CkanPackage.builder()
                                        .id("id")
                                        .title("title")
                                        .build())
                                )
                                .build())

                        .build());

        when(beaconFilteringTermsService.listFilteringTerms(any()))
                .thenReturn(FacetGroup.builder()
                        .key("beacon")
                        .label("label")
                        .facets(List.of(
                                Facet.builder()
                                        .key("key")
                                        .label("label")
                                        .build()
                        ))
                        .build());

        when(keycloakQueryApi.retriveIdpTokens("beaconIdpAlias", "Bearer dummy"))
                .thenReturn(KeycloakTokenResponse.builder()
                        .accessToken("beaconAccessToken")
                        .build());
        when(beaconFilteringTermsService.listFilteringTerms(any()))
                .thenReturn(FacetGroup.builder()
                        .key("beacon")
                        .label("label")
                        .facets(List.of(
                                Facet.builder()
                                        .key("key")
                                        .label("label")
                                        .build()
                        ))
                        .build());

        when(keycloakQueryApi.retriveIdpTokens("beaconIdpAlias", "Bearer dummy"))
                .thenReturn(KeycloakTokenResponse.builder()
                        .accessToken("beaconAccessToken")
                        .build());

        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .facetGroup("beacon")
                                .facet("key")
                                .value("value")
                                .build(
                                )))
                .build();
        var actual = underTest.search(query, "dummy");

        verify(keycloakQueryApi).retriveIdpTokens(any(), any());
        verify(beaconQueryApi).listIndividuals(any(), any());
        verify(ckanQueryApi).packageSearch(any(), any(), any(), any(), any(), any(), any());
        verify(beaconFilteringTermsService).listFilteringTerms(any());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(DatasetsSearchResponse.builder()
                        .count(1)
                        .facetGroupCount(Map.of(
                                "ckan", 1,
                                "beacon", 1
                        ))
                        .results(List.of(
                                SearchedDataset.builder()
                                        .id("id")
                                        .title("title")
                                        .themes(List.of())
                                        .build()
                        ))
                        .facetGroups(List.of(
                                FacetGroup.builder()
                                        .key("beacon")
                                        .label("label")
                                        .facets(List.of(
                                                Facet.builder()
                                                        .key("key")
                                                        .label("label")
                                                        .build()
                                        ))
                                        .build(),
                                FacetGroup.builder()
                                        .key("ckan")
                                        .label("DCAT-AP")
                                        .facets(List.of())
                                        .build()
                        ))
                        .build());
    }
}
