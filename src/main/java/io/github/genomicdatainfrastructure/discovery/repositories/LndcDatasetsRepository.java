// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.repositories;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.services.CkanFacetsQueryBuilder;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.arc.lookup.LookupUnlessProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@LookupIfProperty(name = "repositories.lndc", stringValue = "true")
@ApplicationScoped
public class LndcDatasetsRepository implements DatasetsRepository {

    @Override
    public PackagesSearchResponse search(DatasetSearchQuery query, String accessToken) {
        throw new NotImplementedException();
    }

    @Override

    public CkanPackageShowResponse retrieveCkanPackage(String id, String accessToken) {
        throw new NotImplementedException();
    }
}
