// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.applications.ports;

import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;

import java.util.List;

public interface DatasetsRepository {

    List<SearchedDataset> search(List<String> datasetIds,
            String sort,
            Integer rows,
            Integer start,
            String accessToken);
}
