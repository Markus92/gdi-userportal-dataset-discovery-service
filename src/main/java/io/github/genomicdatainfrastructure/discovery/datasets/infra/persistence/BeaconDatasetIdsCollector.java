package io.github.genomicdatainfrastructure.discovery.datasets.infra.persistence;

import io.github.genomicdatainfrastructure.discovery.core.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResultSet;
import io.github.genomicdatainfrastructure.discovery.utils.BeaconIndividualsRequestMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ApplicationScoped
public class BeaconDatasetIdsCollector implements DatasetIdsCollector {

    private static final String BEACON_DATASET_TYPE = "dataset";

    private final BeaconQueryApi beaconQueryApi;
    private final BeaconAuth beaconAuth;

    @Inject
    public BeaconDatasetIdsCollector(BeaconQueryApi beaconQueryApi, BeaconAuth beaconAuth) {
        this.beaconQueryApi = beaconQueryApi;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public Set<String> collect(DatasetSearchQuery query, String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);

        var beaconQuery = BeaconIndividualsRequestMapper.from(query);

        if (beaconQuery.getQuery().getFilters().isEmpty()) {
            return Set.of();
        }

        var response = beaconQueryApi.listIndividuals(beaconAuthorization, beaconQuery);

        var nonNullResultSets = ofNullable(response)
                .map(BeaconIndividualsResponse::getResponse)
                .map(BeaconIndividualsResponseContent::getResultSets)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        var datasetIds = nonNullResultSets.stream()
                .filter(Objects::nonNull)
                .filter(it -> BEACON_DATASET_TYPE.equals(it.getSetType()))
                .filter(it -> isNotBlank(it.getId()))
                .filter(it -> it.getResultsCount() != null && it.getResultsCount() > 0)
                .map(BeaconResultSet::getId)
                .collect(Collectors.toSet());

        return datasetIds;
    }
}
