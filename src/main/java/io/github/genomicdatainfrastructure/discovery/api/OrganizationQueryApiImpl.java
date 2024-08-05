// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.services.RetrieveOrganizationsService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
public class OrganizationQueryApiImpl implements OrganizationQueryApi {

    private final RetrieveOrganizationsService retrieveOrganizationsService;

    @Override
    public Response retrieveOrganizations() {
        var content = retrieveOrganizationsService.get();
        return Response.ok(content).build();
    }
}
