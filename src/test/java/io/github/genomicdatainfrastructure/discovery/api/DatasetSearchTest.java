// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

@QuarkusTest
class DatasetSearchTest extends BaseTest {

    @Test
    void can_anonymously_search_datasets() {
        var query = DatasetSearchQuery.builder()
                .build();
        given()
                .contentType("application/json")
                .body(query)
                .when()
                .post("/api/v1/datasets/search")
                .then()
                .statusCode(200)
                .body("count", equalTo(1167));
    }

    @Test
    void can_search_datasets() {
        var query = DatasetSearchQuery.builder()
                .build();
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(query)
                .when()
                .post("/api/v1/datasets/search")
                .then()
                .statusCode(200)
                .body("count", equalTo(1167));
    }
}
