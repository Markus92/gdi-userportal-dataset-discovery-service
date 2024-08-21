// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.applications.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;

import java.util.List;

public interface DatasetIdsCollector {

    List<String> collect(DatasetSearchQuery query, String accessToken);
}
