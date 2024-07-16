// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDistribution;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import io.github.genomicdatainfrastructure.discovery.utils.PackageShowMapper;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

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
                .keywords(List.of())
                .contact(List.of())
                .creator(List.of())
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
                .title("title")
                .notes("notes")
                .theme(List.of(CkanValueLabel.builder()
                        .displayName("theme")
                        .name("theme-name")
                        .build()))
                .publisherName("publisherName")
                .organization(CkanOrganization.builder()
                        .title("organization")
                        .build())
                .metadataCreated("2024-03-19T13:37:05.472970")
                .metadataModified("2024-03-19T13:37:05.472970")
                .tags(List.of(CkanTag.builder()
                        .displayName("key-tag")
                        .id("tag-id")
                        .name("key")
                        .build()))
                .url("url")
                .language(List.of(
                        CkanValueLabel.builder()
                                .displayName("language")
                                .name("en")
                                .build()))
                .contactPoint(List.of(
                        ContactPoint.builder()
                                .contactEmail("contactEmail")
                                .contactName("contactName")
                                .contactUri("contactUri")
                                .build()
                ))
                .creator(List.of(
                        Creator.builder()
                                .creatorName("creatorName")
                                .creatorIdentifier("creatorIdentifier")
                                .build()
                ))
                .hasVersion(List.of(
                        CkanValueLabel.builder()
                                .displayName("version")
                                .name("1")
                                .build()))
                .accessRights(CkanValueLabel.builder()
                        .displayName("accessRights")
                        .name("public")
                        .build())
                .conformsTo(List.of(
                        CkanValueLabel.builder()
                                .displayName("conformsTo")
                                .name("conforms")
                                .build()))
                .provenance("provenance")
                .spatialUri(CkanValueLabel.builder()
                        .displayName("spatial")
                        .name("uri")
                        .build())
                .resources(List.of(
                        CkanResource.builder()
                                .id("resource_id")
                                .name("resource_name")
                                .description("resource_description")
                                .format(CkanValueLabel.builder()
                                        .displayName("format")
                                        .name("pdf")
                                        .build())
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
                .title("title")
                .description("notes")
                .themes(List.of(
                        ValueLabel.builder()
                                .value("theme-name")
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
                                .value("en")
                                .label("language")
                                .build()
                ))
                .contact(List.of(
                        ValueLabel.builder()
                                .value("contactUri")
                                .label("contactName")
                                .build()))
                .creator(List.of(
                        ValueLabel.builder()
                                .label("creatorName")
                                .value("creatorIdentifier")
                                .build()
                ))
                .hasVersions(List.of(
                        ValueLabel.builder()
                                .value("1")
                                .label("version")
                                .build()
                ))
                .accessRights(ValueLabel.builder()
                        .value("public")
                        .label("accessRights")
                        .build())
                .conformsTo(List.of(
                        ValueLabel.builder()
                                .value("conforms")
                                .label("conformsTo")
                                .build()
                ))
                .provenance("provenance")
                .keywords(List.of(ValueLabel.builder()
                        .label("key-tag")
                        .value("key")
                        .build()))
                .spatial(ValueLabel.builder()
                        .value("uri")
                        .label("spatial")
                        .build())
                .distributions(List.of(
                        RetrievedDistribution.builder()
                                .id("resource_id")
                                .title("resource_name")
                                .description("resource_description")
                                .format(ValueLabel.builder()
                                        .value("pdf")
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
