package io.github.genomicdatainfrastructure.discovery.datasets.applications.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;

import java.util.List;

public interface FacetsBuilder {
    List<DatasetSearchQueryFacet> buildFacets(DatasetSearchQuery query);
}
