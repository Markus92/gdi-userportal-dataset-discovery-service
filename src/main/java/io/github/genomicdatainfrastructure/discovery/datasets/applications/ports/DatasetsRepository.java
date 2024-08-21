// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.applications.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;

import java.util.Set;

public interface DatasetsRepository {
    Set<SearchedDataset> search(Set<String> datasetIds, String accessToken);
}
