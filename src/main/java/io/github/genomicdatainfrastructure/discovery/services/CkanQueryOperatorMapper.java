// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CkanQueryOperatorMapper {

    private final String AND = " AND ";
    private final String OR = " OR ";

    public String getOperator(String operator) {
        return operator.equals(CkanQueryOperator.Or) ? OR : AND;
    }
}
