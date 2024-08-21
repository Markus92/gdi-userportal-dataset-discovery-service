package io.github.genomicdatainfrastructure.discovery.datasets.infra.persistence;

import io.github.genomicdatainfrastructure.discovery.core.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService.BEACON_FACET_GROUP;

public class BeaconFacetsBuilder implements FacetsBuilder {
    private final BeaconFilteringTermsService service;
    private final BeaconAuth beaconAuth;

    @Inject
    public BeaconFacetsBuilder(BeaconFilteringTermsService service, BeaconAuth beaconAuth) {
        this.service = service;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public List<DatasetSearchQueryFacet> buildFacets(DatasetSearchQuery query, String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);

        var facetGroupCount = new HashMap<String, Integer>();
        facetGroupCount.put(BEACON_FACET_GROUP, 0);

        var facetGroups = new ArrayList<FacetGroup>();
        facetGroups.add(service.listFilteringTerms(beaconAuthorization));

    }
}
