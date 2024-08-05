// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@QuarkusTest
class RetrieveOrganizationsTest extends BaseTest {

    @Test
    void can_retrieve_organizations() {
        given()
                .when()
                .get("/api/v1/organizations")
                .then()
                .statusCode(200);
    }

    @Test
    void can_retrieve_correct_organizations_data() {
        var response = given()
                .when()
                .get("/api/v1/organizations");

        var body = response.getBody().as(List.class);
        assertThat(body).isEqualTo(List.of(
                "cscfi",
                "ega",
                "instituto-superior-tecnico",
                "lnds",
                "nbis",
                "radboud",
                "umcg",
                "university-of-oslo"
        ));
    }
}
