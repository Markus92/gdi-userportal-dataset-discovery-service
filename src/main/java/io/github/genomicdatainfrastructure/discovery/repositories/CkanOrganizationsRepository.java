// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.repositories;

import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class CkanOrganizationsRepository {

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanOrganizationsRepository(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    public List<String> retrieveOrganizations() {
        return ckanQueryApi.organizationList().getResult();
    }
}
