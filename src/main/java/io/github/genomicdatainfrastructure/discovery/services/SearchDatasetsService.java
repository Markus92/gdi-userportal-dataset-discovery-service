// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.repositories.DatasetsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class SearchDatasetsService {

    @Inject
    Instance<DatasetsRepository> datasetsRepository;

    public DatasetsSearchResponse search(DatasetSearchQuery query, String accessToken) {
        return datasetsRepository.get().search(query, accessToken);
    }
}
