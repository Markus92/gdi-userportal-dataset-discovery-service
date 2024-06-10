// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
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
        var query = new DatasetSearchQuery();
        query.setFacets(List.of());
        var expected = "";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);
        assertEquals(expected, actual);
    }

    @Test
    void can_parse_if_null_list() {
        String expected = "";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(new DatasetSearchQuery());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void can_parse_with_and_operator(String value) {
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

        var query = new DatasetSearchQuery();
        query.setFacets(facets);
        query.setOperator(DatasetSearchQuery.OperatorEnum.AND);

        var expected = "field1:(\"value1\" AND \"value2\") AND field2:(\"value3\")";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void can_parse_with_or_operator(String value) {
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

        var query = new DatasetSearchQuery();
        query.setFacets(facets);
        query.setOperator(DatasetSearchQuery.OperatorEnum.OR);

        var expected = "field1:(\"value1\" OR \"value2\") OR field2:(\"value3\")";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);
        assertEquals(expected, actual);
    }
}
