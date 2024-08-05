// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.repositories.CkanOrganizationsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class RetrieveOrganizationsService {

    @Inject
    CkanOrganizationsRepository organizationsRepository;

    public List<String> get() {
        return organizationsRepository.retrieveOrganizations();
    }
}
