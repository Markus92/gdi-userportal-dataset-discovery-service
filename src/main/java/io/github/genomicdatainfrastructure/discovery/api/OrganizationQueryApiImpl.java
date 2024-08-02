// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.services.RetrieveOrganizationsService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class OrganizationQueryApiImpl implements OrganizationQueryApi {

    private final RetrieveOrganizationsService retrieveOrganizationsService;

    @Override
    public List<String> retrieveOrganizations() {
        return retrieveOrganizationsService.get();
    }
}
