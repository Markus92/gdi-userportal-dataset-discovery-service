package io.github.genomicdatainfrastructure.discovery.datasets.applications.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;

import java.util.Set;

public interface DatasetIdsCollector {
    Set<String> collect(DatasetSearchQuery query, String accessToken);
}
