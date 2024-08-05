// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.model.DatasetOrganization;
import io.github.genomicdatainfrastructure.discovery.repositories.CkanOrganizationsRepository;
import io.github.genomicdatainfrastructure.discovery.utils.DatasetOrganizationMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveOrganizationsService {

    private final CkanOrganizationsRepository organizationsRepository;

    public List<DatasetOrganization> retrieve(Integer limit) {
        var ckanOrganizations = organizationsRepository.retrieveOrganizations(limit);

        return ckanOrganizations.stream()
                .map(DatasetOrganizationMapper::from)
                .toList();
    }
}
