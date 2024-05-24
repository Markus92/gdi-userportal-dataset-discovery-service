// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequest;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestMeta;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestQuery;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestQueryFilter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestQueryPagination;
import java.util.List;

class BeaconIndividualsRequestMapperTest {

    @Test
    void accepts_null_query() {
        var actual = BeaconIndividualsRequestMapper.from(null);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconIndividualsRequest.builder()
                        .meta(BeaconIndividualsRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconIndividualsRequestQuery.builder()
                                .includeResultsetResponses("HIT")
                                .requestedGranularity("record")
                                .testMode(false)
                                .pagination(BeaconIndividualsRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of())
                                .build())
                        .build());
    }

    @Test
    void accepts_empty_query() {
        var actual = BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(null)
                .build());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconIndividualsRequest.builder()
                        .meta(BeaconIndividualsRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconIndividualsRequestQuery.builder()
                                .includeResultsetResponses("HIT")
                                .requestedGranularity("record")
                                .testMode(false)
                                .pagination(BeaconIndividualsRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of())
                                .build())
                        .build());
    }

    @Test
    void accepts_empty_facets() {
        var actual = BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(List.of())
                .build());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconIndividualsRequest.builder()
                        .meta(BeaconIndividualsRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconIndividualsRequestQuery.builder()
                                .includeResultsetResponses("HIT")
                                .requestedGranularity("record")
                                .testMode(false)
                                .pagination(BeaconIndividualsRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of())
                                .build())
                        .build());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void can_parse(String value) {
        var actual = BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .facetGroup("beacon")
                                .facet("dummy")
                                .value("dummy_value")
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .facetGroup("beacon")
                                .facet("dummy")
                                .value(value)
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .facetGroup("beacon")
                                .facet(value)
                                .value("dummy_value_2")
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .facetGroup(value)
                                .facet("dummy_2")
                                .value("dummy_value_3")
                                .build()
                ))
                .build());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconIndividualsRequest.builder()
                        .meta(BeaconIndividualsRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconIndividualsRequestQuery.builder()
                                .includeResultsetResponses("HIT")
                                .requestedGranularity("record")
                                .testMode(false)
                                .pagination(BeaconIndividualsRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of(
                                        BeaconIndividualsRequestQueryFilter.builder()
                                                .id("dummy_value")
                                                .scope("individual")
                                                .build(),
                                        BeaconIndividualsRequestQueryFilter.builder()
                                                .id("dummy_value_2")
                                                .scope("individual")
                                                .build()
                                ))
                                .build())
                        .build());
    }
}
