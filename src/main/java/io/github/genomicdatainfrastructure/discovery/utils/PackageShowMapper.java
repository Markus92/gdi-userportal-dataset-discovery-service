// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.utils;

import java.util.List;
import java.util.Objects;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
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
                .title(ckanPackage.getTitle())
                .description(ckanPackage.getNotes())
                .themes(values(ckanPackage.getTheme()))
                .publisherName(ckanPackage.getPublisherName())
                .catalogue(catalogue)
                .createdAt(parse(ckanPackage.getMetadataCreated()))
                .modifiedAt(parse(ckanPackage.getMetadataModified()))
                .url(ckanPackage.getUrl())
                .languages(values(ckanPackage.getLanguage()))
                .contact(value(ckanPackage.getContactUri()))
                .accessRights(value(ckanPackage.getAccessRights()))
                .provenance(ckanPackage.getProvenance())
                .spatial(value(ckanPackage.getSpatialUri()))
                .distributions(distributions(ckanPackage))
                .keywords(keywords(ckanPackage))
                .contacts(contactPoints(ckanPackage.getContacts()))
                .datasetRelationships(relations(ckanPackage.getDatasetRelationships()))
                .dataDictionary(dictionary(ckanPackage.getDataDictionary()))
                .build();
    }

    private List<ContactPoints> contactPoints(List<CkanContactPoints> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::contactPoint)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<DatasetRelationEntry> relations(List<CkanDatasetRelationEntry> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::relation)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<DatasetDictionaryEntry> dictionary(List<CkanDatasetDictionaryEntry> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::dictionaryEntry)
                .filter(Objects::nonNull)
                .toList();
    }

    private ContactPoints contactPoint(CkanContactPoints value) {
        return ofNullable(value)
                .filter(Objects::nonNull)
                .map(it -> ContactPoints.builder()
                        .name(it.getName())
                        .email(it.getEmail())
                        .build())
                .orElse(null);
    }

    private DatasetRelationEntry relation(CkanDatasetRelationEntry value) {
        return ofNullable(value)
                .filter(Objects::nonNull)
                .map(it -> DatasetRelationEntry.builder()
                        .relation(it.getRelation())
                        .target(it.getTarget())
                        .build())
                .orElse(null);
    }

    private DatasetDictionaryEntry dictionaryEntry(CkanDatasetDictionaryEntry value) {
        return ofNullable(value)
                .filter(Objects::nonNull)
                .map(it -> DatasetDictionaryEntry.builder()
                        .name(it.getName())
                        .type(it.getType())
                        .description(it.getDescription())
                        .build())
                .orElse(null);
    }

    private List<ValueLabel> values(List<CkanValueLabel> values) {
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

    private ValueLabel value(CkanValueLabel value) {
        return ofNullable(value)
                .filter(Objects::nonNull)
                .map(it -> ValueLabel.builder()
                        .value(it.getName())
                        .label(it.getDisplayName())
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
