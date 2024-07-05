// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.utils;

import static java.util.Optional.ofNullable;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequest;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestMeta;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestQuery;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestQueryFilter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequestQueryPagination;
import lombok.experimental.UtilityClass;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class BeaconIndividualsRequestMapper {

    private static final String BEACON_FACET_GROUP = "beacon";
    private static final String SCOPE = "individual";
    private static final String INCLUDE_RESULTSET_RESPONSES = "HIT";
    private static final String REQUESTED_GRANULARITY = "record";

    public BeaconIndividualsRequest from(
            DatasetSearchQuery query
    ) {
        var nonNullFacets = ofNullable(query)
                .map(DatasetSearchQuery::getFacets)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        var beaconFilters = nonNullFacets.stream()
                .filter(it -> BEACON_FACET_GROUP.equals(it.getFacetGroup()))
                .map(DatasetSearchQueryFacet::getValue)
                .filter(StringUtils::isNotBlank)
                .map(it -> BeaconIndividualsRequestQueryFilter.builder()
                        .id(it)
                        .scope(SCOPE)
                        .build())
                .toList();

        return BeaconIndividualsRequest.builder()
                .meta(new BeaconIndividualsRequestMeta())
                .query(BeaconIndividualsRequestQuery.builder()
                        .includeResultsetResponses(INCLUDE_RESULTSET_RESPONSES)
                        .requestedGranularity(REQUESTED_GRANULARITY)
                        .testMode(false)
                        .pagination(new BeaconIndividualsRequestQueryPagination())
                        .filters(beaconFilters)
                        .build())
                .build();
    }
}
