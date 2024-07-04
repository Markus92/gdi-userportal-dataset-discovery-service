// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.repositories;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.NotImplementedException;

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
