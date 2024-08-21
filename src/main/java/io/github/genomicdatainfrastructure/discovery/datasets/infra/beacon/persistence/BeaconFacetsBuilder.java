// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infra.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.datasets.infra.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService;
import jakarta.inject.Inject;

public class BeaconFacetsBuilder implements FacetsBuilder {

    private final BeaconFilteringTermsService service;
    private final BeaconAuth beaconAuth;

    @Inject
    public BeaconFacetsBuilder(BeaconFilteringTermsService service, BeaconAuth beaconAuth) {
        this.service = service;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public FacetGroup buildFacets(DatasetSearchQuery query, String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);
        return service.listFilteringTerms(beaconAuthorization);
    }
}
