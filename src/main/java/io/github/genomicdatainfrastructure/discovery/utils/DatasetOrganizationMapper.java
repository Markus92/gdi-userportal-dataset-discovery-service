// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.utils;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DatasetOrganizationMapper {

    public DatasetOrganization from(CkanOrganization organization) {
        return DatasetOrganization.builder()
                .title(organization.getTitle())
                .description(organization.getDescription())
                .imageUrl(organization.getImageUrl())
                .name(organization.getName())
                .build();
    }
}
