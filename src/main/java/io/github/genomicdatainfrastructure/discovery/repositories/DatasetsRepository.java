// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.repositories;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;

public interface DatasetsRepository {

    PackagesSearchResponse search(DatasetSearchQuery query, String accessToken);

    CkanPackageShowResponse retrieveCkanPackage(String id, String accessToken);
}
