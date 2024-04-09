// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import java.util.List;
import java.util.Objects;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import lombok.experimental.UtilityClass;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@UtilityClass
public class CkanFacetsQueryBuilder {

    private final String CKAN_FACET_GROUP = "ckan";
    private final String QUOTED_VALUE = "\"%s\"";
    private final String FACET_PATTERN = "%s:(%s)";
    private final String AND = " AND ";

    public String buildFacetQuery(List<DatasetSearchQueryFacet> facets) {
        var nonNullFacets = ofNullable(facets)
                .orElseGet(List::of)
                .stream()
                .filter(CkanFacetsQueryBuilder::isCkanGroupAndFacetIsNotBlank)
                .collect(groupingBy(DatasetSearchQueryFacet::getFacet));

        return nonNullFacets.entrySet().stream()
                .map(entry -> getFacetQuery(entry.getKey(), entry.getValue()))
                .collect(joining(AND));
    }

    private Boolean isCkanGroupAndFacetIsNotBlank(DatasetSearchQueryFacet facet) {
        return Objects.equals(CKAN_FACET_GROUP, facet.getFacetGroup()) &&
                nonNull(facet.getFacet()) &&
                !facet.getFacet().isBlank() &&
                nonNull(facet.getValue()) &&
                !facet.getValue().isBlank();
    }

    private String getFacetQuery(String key, List<DatasetSearchQueryFacet> facets) {
        var values = facets.stream()
                .map(facet -> QUOTED_VALUE.formatted(facet.getValue()))
                .collect(joining(AND));

        return FACET_PATTERN.formatted(key, values);
    }
}
