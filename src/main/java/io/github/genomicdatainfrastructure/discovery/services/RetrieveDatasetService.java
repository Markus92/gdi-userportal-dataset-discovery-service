// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.repositories.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.exceptions.DatasetNotFoundException;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class RetrieveDatasetService {

    @Inject
    Instance<DatasetsRepository> datasetsRepository;

    public RetrievedDataset retrieve(String id, String accessToken) {
        var response = retrieveCkanPackage(id, accessToken);
        return PackageShowMapper.from(response.getResult());
    }

    public CkanPackageShowResponse retrieveCkanPackage(String id, String accessToken) {
        try {
            return datasetsRepository.get().retrieveCkanPackage(id, accessToken);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new DatasetNotFoundException(id);
            }
            throw e;
        }
    }
}
