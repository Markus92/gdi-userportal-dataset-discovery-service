// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;

class CkanFacetsQueryBuilderTest {

    @Test
    void can_parse_if_empty_list() {
        var expected = "";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(List.of());
        assertEquals(expected, actual);
    }

    @Test
    void can_parse_if_null_list() {
        String expected = "";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(null);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void can_parse(String value) {
        var facets = List.of(
                new DatasetSearchQueryFacet("ckan", "field1", "value1"),
                new DatasetSearchQueryFacet("ckan", "field1", "value2"),
                new DatasetSearchQueryFacet("ckan", "field2", "value3"),
                new DatasetSearchQueryFacet("dummy", "field2", "value"),
                new DatasetSearchQueryFacet(null, "field2", "value"),
                new DatasetSearchQueryFacet(null, null, "value"),
                new DatasetSearchQueryFacet("ckan", value, "value3"),
                new DatasetSearchQueryFacet("ckan", "field2", value),
                new DatasetSearchQueryFacet(value, "field2", "value3")
        );

        var expected = "field1:(\"value1\" AND \"value2\") AND field2:(\"value3\")";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(facets);
        assertEquals(expected, actual);
    }
}
