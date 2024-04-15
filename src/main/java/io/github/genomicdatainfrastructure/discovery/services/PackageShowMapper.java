// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import java.util.List;
import java.util.Objects;

import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDistribution;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanOrganization;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanResource;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanTag;
import lombok.experimental.UtilityClass;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class PackageShowMapper {

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    public RetrievedDataset from(CkanPackage ckanPackage) {
        var catalogue = ofNullable(ckanPackage.getOrganization())
                .map(CkanOrganization::getTitle)
                .orElse(null);

        return RetrievedDataset.builder()
                .id(ckanPackage.getId())
                .identifier(ckanPackage.getIdentifier())
                .title(ckanPackage.getName())
                .description(ckanPackage.getNotes())
                .themes(values(ckanPackage.getTheme()))
                .publisherName(ckanPackage.getPublisherName())
                .catalogue(catalogue)
                .createdAt(parse(ckanPackage.getMetadataCreated()))
                .modifiedAt(parse(ckanPackage.getMetadataModified()))
                .url(ckanPackage.getUrl())
                .languages(values(ckanPackage.getLanguage()))
                .contact(value(ckanPackage.getContactUri()))
                .hasVersions(values(ckanPackage.getHasVersion()))
                .accessRights(value(ckanPackage.getAccessRights()))
                .conformsTo(values(ckanPackage.getConformsTo()))
                .provenance(ckanPackage.getProvenance())
                .spatial(value(ckanPackage.getSpatialUri()))
                .distributions(distributions(ckanPackage))
                .keywords(keywords(ckanPackage))
                .build();
    }

    private List<ValueLabel> values(List<String> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::value)
                .filter(Objects::nonNull)
                .toList();
    }

    private ValueLabel value(String value) {
        return ofNullable(value)
                .filter(Objects::nonNull)
                .filter(not(String::isBlank))
                .map(it -> ValueLabel.builder()
                        .value(it)
                        .label(it)
                        .build())
                .orElse(null);
    }

    private LocalDateTime parse(String date) {
        return ofNullable(date)
                .map(it -> LocalDateTime.parse(it, DATE_FORMATTER))
                .orElse(null);
    }

    private List<RetrievedDistribution> distributions(CkanPackage ckanPackage) {
        return ofNullable(ckanPackage.getResources())
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::distribution)
                .toList();
    }

    private List<ValueLabel> keywords(CkanPackage ckanPackage) {
        return ofNullable(ckanPackage.getTags())
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::keyword)
                .toList();
    }

    private RetrievedDistribution distribution(CkanResource ckanResource) {
        return RetrievedDistribution.builder()
                .id(ckanResource.getId())
                .title(ckanResource.getName())
                .description(ckanResource.getDescription())
                .format(value(ckanResource.getFormat()))
                .uri(ckanResource.getUri())
                .createdAt(parse(ckanResource.getCreated()))
                .modifiedAt(parse(ckanResource.getLastModified()))
                .build();
    }

    private ValueLabel keyword(CkanTag ckanTag) {
        return ValueLabel.builder()
                .label(ckanTag.getDisplayName())
                .value(ckanTag.getName())
                .build();
    }
}
