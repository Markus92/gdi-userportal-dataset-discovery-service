// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanOrganization;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import io.github.genomicdatainfrastructure.discovery.utils.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.utils.DatasetOrganizationMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FACET_GROUP;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_IDENTIFIER_FIELD;
import static java.util.Optional.ofNullable;

@ApplicationScoped
public class CkanDatasetsRepository implements DatasetsRepository {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanDatasetsRepository(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public List<SearchedDataset> search(List<String> datasetIds,
            String returnFields,
            String sort,
            Integer rows,
            Integer start,
            String accessToken) {

        if (datasetIds == null || datasetIds.isEmpty()) {
            return List.of();
        }

        var facets = datasetIds
                .stream()
                .map(id -> DatasetSearchQueryFacet
                        .builder()
                        .facetGroup(CKAN_FACET_GROUP)
                        .facet(CKAN_IDENTIFIER_FIELD)
                        .value(id)
                        .build())
                .toList();

        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(DatasetSearchQuery
                .builder()
                .facets(facets)
                .build());

        var response = ckanQueryApi.packageSearch(
                "",
                facetsQuery,
                returnFields,
                sort,
                rows,
                start,
                "",
                accessToken
        );

        return results(response.getResult());
    }

    private List<SearchedDataset> results(PackagesSearchResult result) {
        var nonNullPackages = ofNullable(result)
                .map(PackagesSearchResult::getResults)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        return nonNullPackages.stream()
                .map(this::result)
                .toList();
    }

    private SearchedDataset result(CkanPackage dataset) {
        var catalogue = ofNullable(dataset.getOrganization())
                .map(CkanOrganization::getTitle)
                .orElse(null);

        return SearchedDataset.builder()
                .id(dataset.getId())
                .identifier(dataset.getIdentifier())
                .title(dataset.getTitle())
                .description(dataset.getNotes())
                .themes(values(dataset.getTheme()))
                .catalogue(catalogue)
                .organization(DatasetOrganizationMapper.from(dataset.getOrganization()))
                .modifiedAt(parse(dataset.getMetadataModified()))
                .createdAt(parse(dataset.getMetadataCreated()))
                .build();
    }

    private List<ValueLabel> values(List<CkanValueLabel> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(this::value)
                .filter(Objects::nonNull)
                .toList();
    }

    private ValueLabel value(CkanValueLabel value) {
        return ofNullable(value)
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
}
