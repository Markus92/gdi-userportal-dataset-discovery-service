// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infra.beacon.auth;

import io.github.genomicdatainfrastructure.discovery.remote.keycloak.api.KeycloakQueryApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Set;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class BeaconAuth {

    private static final String BEACON_ACCESS_TOKEN_INFO = "Skipping beacon search, user is not authorized or the token is invalid.";
    private static final String BEARER_PATTERN = "Bearer %s";
    private static final Set<Integer> SKIP_BEACON_QUERY_STATUS = Set.of(400, 401, 403);

    private final KeycloakQueryApi keycloakQueryApi;
    private final String beaconIdpAlias;

    public BeaconAuth(
            @RestClient KeycloakQueryApi keycloakQueryApi,
            @ConfigProperty(name = "quarkus.rest-client.keycloak_yaml.beacon_idp_alias") String beaconIdpAlias
    ) {
        this.keycloakQueryApi = keycloakQueryApi;
        this.beaconIdpAlias = beaconIdpAlias;
    }

    public String retrieveAuthorization(String accessToken) {
        if (accessToken == null) {
            return null;
        }

        var keycloakAuthorization = BEARER_PATTERN.formatted(accessToken);

        try {
            var response = keycloakQueryApi.retriveIdpTokens(beaconIdpAlias, keycloakAuthorization);
            return BEARER_PATTERN.formatted(response.getAccessToken());
        } catch (WebApplicationException exception) {
            if (SKIP_BEACON_QUERY_STATUS.contains(exception.getResponse().getStatus())) {
                log.log(Level.INFO, BEACON_ACCESS_TOKEN_INFO);
                log.log(Level.WARNING, exception, exception::getMessage);
                return null;
            }
            throw exception;
        }
    }
}
