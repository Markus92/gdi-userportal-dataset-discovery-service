// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.DatasetOrganization;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class RetrieveOrganizationsTest extends BaseTest {

    @Test
    void can_retrieve_organizations() {
        var response = given()
                .when()
                .get("/api/v1/organizations");

        var actual = response.getBody()
                .as(new TypeRef<List<DatasetOrganization>>() {
                });

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        DatasetOrganization.builder()
                                .id("3e0a548c-eef4-404b-96ec-dd95b549643c")
                                .name("instituto-superior-tecnico")
                                .title("Instituto Superior Técnico")
                                .numberOfDatasets(1)
                                .build(),
                        DatasetOrganization.builder()
                                .id("d9133f3e-8747-4764-a3c6-27f93a37c38d")
                                .title("CSC FI")
                                .name("csc-fi")
                                .description(
                                        "The tools CSC provides for scientific computing and data management create a stepping stone for breakthroughs.")
                                .imageUrl(
                                        "https://csc.fi/app/uploads/2023/09/CSC_logo_no_tagline.svg")
                                .numberOfDatasets(1)
                                .build(),
                        DatasetOrganization.builder()
                                .id("da348a59-8632-4c28-af3e-786e72448b8c")
                                .name("ega")
                                .title("EGA")
                                .numberOfDatasets(4)
                                .build(),
                        DatasetOrganization.builder()
                                .id("1acb508d-371c-4a43-b143-65ec2e0f1f82")
                                .name("lnds")
                                .title("LNDS")
                                .numberOfDatasets(1)
                                .build(),
                        DatasetOrganization.builder()
                                .id("d4f783b7-64e9-4552-9667-c81a56259bbb")
                                .name("nbis")
                                .title("NBIS")
                                .numberOfDatasets(3)
                                .build(),
                        DatasetOrganization.builder()
                                .id("c24f44ff-a0e0-4171-bf33-a9b6e2ea9298")
                                .name("umcg")
                                .title("UMCG")
                                .numberOfDatasets(5)
                                .build(),
                        DatasetOrganization.builder()
                                .id("5e48036d-4e38-40d8-ba8e-8cce6faeb3be")
                                .name("university-of-oslo")
                                .title("University of Oslo")
                                .numberOfDatasets(1)
                                .build()
                );
    }
}
