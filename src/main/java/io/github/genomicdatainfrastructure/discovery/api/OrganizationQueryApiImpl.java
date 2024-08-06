// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.services.RetrieveOrganizationsService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class OrganizationQueryApiImpl implements OrganizationQueryApi {

    private final RetrieveOrganizationsService retrieveOrganizationsService;

    @Override
    public Response retrieveOrganizations(Integer limit) {
        var nonNullLimit = ofNullable(limit).orElse(100);
        var content = retrieveOrganizationsService.retrieve(nonNullLimit);
        return Response.ok(content).build();
    }
}
