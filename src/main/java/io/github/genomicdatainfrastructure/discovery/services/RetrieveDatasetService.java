// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.exceptions.DatasetNotFoundException;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse;
import io.github.genomicdatainfrastructure.discovery.utils.PackageShowMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class RetrieveDatasetService {

    private final CkanQueryApi ckanQueryApi;

    public RetrieveDatasetService(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    public RetrievedDataset retrieve(String id, String accessToken) {
        var response = retrieveCkanPackage(id, accessToken);
        return PackageShowMapper.from(response.getResult());
    }

    public CkanPackageShowResponse retrieveCkanPackage(String id, String accessToken) {
        try {
            return ckanQueryApi.packageShow(id, accessToken);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new DatasetNotFoundException(id);
            }
            throw e;
        }
    }
}
