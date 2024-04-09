// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import static java.time.LocalDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDistribution;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanOrganization;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanResource;
import java.time.format.DateTimeFormatter;

class PackageShowMapperTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    @Test
    void accepts_empty_package() {
        var ckanPackage = CkanPackage.builder().build();

        var actual = PackageShowMapper.from(ckanPackage);
        var expected = RetrievedDataset.builder()
                .conformsTo(List.of())
                .distributions(List.of())
                .hasVersions(List.of())
                .languages(List.of())
                .themes(List.of())
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void can_parse() {
        var ckanPackage = CkanPackage.builder()
                .id("id")
                .identifier("identifier")
                .name("name")
                .notes("notes")
                .theme(List.of("theme"))
                .publisherName("publisherName")
                .organization(CkanOrganization.builder()
                        .title("organization")
                        .build())
                .metadataCreated("2024-03-19T13:37:05.472970")
                .metadataModified("2024-03-19T13:37:05.472970")
                .url("url")
                .language(List.of("language"))
                .contactUri("contactUri")
                .hasVersion(List.of("hasVersion"))
                .accessRights("accessRights")
                .conformsTo(List.of("conformsTo"))
                .provenance("provenance")
                .spatialUri("spatialUri")
                .resources(List.of(
                        CkanResource.builder()
                                .id("resource_id")
                                .name("resource_name")
                                .description("resource_description")
                                .format("format")
                                .uri("uri")
                                .created("2024-03-19T13:37:05.472970")
                                .lastModified("2024-03-19T13:37:05.472970")
                                .build()
                ))
                .build();

        var actual = PackageShowMapper.from(ckanPackage);
        var expected = RetrievedDataset.builder()
                .id("id")
                .identifier("identifier")
                .title("name")
                .description("notes")
                .themes(List.of(
                        ValueLabel.builder()
                                .value("theme")
                                .label("theme")
                                .build()
                ))
                .publisherName("publisherName")
                .catalogue("organization")
                .createdAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                .modifiedAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                .url("url")
                .languages(List.of(
                        ValueLabel.builder()
                                .value("language")
                                .label("language")
                                .build()
                ))
                .contact(ValueLabel.builder()
                        .value("contactUri")
                        .label("contactUri")
                        .build())
                .hasVersions(List.of(
                        ValueLabel.builder()
                                .value("hasVersion")
                                .label("hasVersion")
                                .build()
                ))
                .accessRights(ValueLabel.builder()
                        .value("accessRights")
                        .label("accessRights")
                        .build())
                .conformsTo(List.of(
                        ValueLabel.builder()
                                .value("conformsTo")
                                .label("conformsTo")
                                .build()
                ))
                .provenance("provenance")
                .spatial(ValueLabel.builder()
                        .value("spatialUri")
                        .label("spatialUri")
                        .build())
                .distributions(List.of(
                        RetrievedDistribution.builder()
                                .id("resource_id")
                                .title("resource_name")
                                .description("resource_description")
                                .format(ValueLabel.builder()
                                        .value("format")
                                        .label("format")
                                        .build())
                                .uri("uri")
                                .createdAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                                .modifiedAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
