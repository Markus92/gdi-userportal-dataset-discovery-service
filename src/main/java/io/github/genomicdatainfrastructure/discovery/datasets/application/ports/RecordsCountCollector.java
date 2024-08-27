// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;

import java.util.Map;

public interface RecordsCountCollector {

    Map<String, Integer> collectRecordsCount(DatasetSearchQuery query, String accessToken);
}
