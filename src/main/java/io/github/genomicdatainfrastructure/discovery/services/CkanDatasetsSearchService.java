// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.repositories.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class CkanDatasetsSearchService implements DatasetsSearchService {

    @Inject
    Instance<DatasetsRepository> datasetsRepository;

    @Override
    public DatasetsSearchResponse search(DatasetSearchQuery query, String accessToken) {
        var response = datasetsRepository.get().search(query, accessToken);

        return PackagesSearchResponseMapper.from(response);
    }
}
