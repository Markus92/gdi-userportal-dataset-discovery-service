// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.core.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResultSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService.BEACON_FACET_GROUP;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@ApplicationScoped
public class SearchDatasetsService {

    @Inject
    Instance<IDatasetsRepository> datasetsRepository;

    @Inject
    Instance<BeaconIndividualsRepository> beaconIndividualsRepository;

    @Inject
    Instance<DatasetQueryBuilder> datasetQueryBuilder;

    @Inject
    Instance<BeaconAuth> beaconAuth;

    @Inject
    Instance<BeaconFilteringTermsService> beaconFilteringTermsService;

    @ConfigProperty(name = "sources.beacon")
    private String useBeacon;

    public DatasetsSearchResponse search(DatasetSearchQuery baseQuery, String accessToken) {
        var datasetsRepository = this.datasetsRepository.get();

        if (useBeacon == "false") {
            return datasetsRepository.search(baseQuery, accessToken);
        }

        var beaconAuthorization = beaconAuth.get().retrieveAuthorization(accessToken);

        if (beaconAuthorization == null) {
            return datasetsRepository.search(baseQuery, accessToken);
        }

        var beaconIndividualsResultSets = beaconIndividualsRepository.get().search(baseQuery,
                beaconAuthorization);
        var datasetsSearchResponse = retrieveDatasetsSearchResponse(baseQuery,
                beaconIndividualsResultSets, accessToken);

        return enhanceDatasetsResponse(beaconAuthorization, datasetsSearchResponse,
                beaconIndividualsResultSets);
    }

    private DatasetsSearchResponse retrieveDatasetsSearchResponse(DatasetSearchQuery baseQuery,
            List<BeaconResultSet> beaconIndividualsResultSets,
            String accessToken) {
        var nonNullFacets = ofNullable(baseQuery.getFacets()).orElseGet(List::of);
        var thereIsAtLeastOneBeaconFilter = nonNullFacets.stream()
                .anyMatch(it -> BEACON_FACET_GROUP.equals(it.getFacetGroup()));

        DatasetsSearchResponse datasetsSearchResponse;

        if (thereIsAtLeastOneBeaconFilter && beaconIndividualsResultSets.isEmpty()) {
            datasetsSearchResponse = DatasetsSearchResponse.builder()
                    .count(0)
                    .build();
        } else {
            var enhancedQuery = datasetQueryBuilder.get().build(baseQuery,
                    beaconIndividualsResultSets);
            datasetsSearchResponse = datasetsRepository.get().search(enhancedQuery, accessToken);
        }

        return datasetsSearchResponse;
    }

    private DatasetsSearchResponse enhanceDatasetsResponse(
            String beaconAuthorization,
            DatasetsSearchResponse datasetsSearchResponse,
            List<BeaconResultSet> resultSets
    ) {
        var facetGroupCount = new HashMap<String, Integer>();
        facetGroupCount.put(BEACON_FACET_GROUP, resultSets.size());
        if (isNotEmpty(datasetsSearchResponse.getFacetGroupCount())) {
            facetGroupCount.putAll(datasetsSearchResponse.getFacetGroupCount());
        }

        var facetGroups = new ArrayList<FacetGroup>();
        facetGroups.add(beaconFilteringTermsService.get().listFilteringTerms(beaconAuthorization));
        if (isNotEmpty(datasetsSearchResponse.getFacetGroups())) {
            facetGroups.addAll(datasetsSearchResponse.getFacetGroups());
        }

        var results = List.<SearchedDataset>of();
        if (isNotEmpty(datasetsSearchResponse.getResults())) {
            var recordCounts = resultSets.stream()
                    .collect(toMap(
                            BeaconResultSet::getId,
                            BeaconResultSet::getResultsCount
                    ));

            results = datasetsSearchResponse.getResults()
                    .stream()
                    .map(it -> it.toBuilder()
                            .recordsCount(recordCounts.get(it.getIdentifier()))
                            .build())
                    .toList();
        }

        return datasetsSearchResponse.toBuilder()
                .facetGroupCount(facetGroupCount)
                .facetGroups(facetGroups)
                .results(results)
                .build();
    }
}
