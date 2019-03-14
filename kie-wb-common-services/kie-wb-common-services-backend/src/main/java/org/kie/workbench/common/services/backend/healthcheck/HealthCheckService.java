/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.healthcheck;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

@Path("/")
@ApplicationScoped
public class HealthCheckService {

    @Inject
    @SuppressWarnings("all")
    private Instance<ServiceCheck> services;

    @GET
    @PermitAll
    @Produces(APPLICATION_JSON)
    @Path("/ready")
    public Response isReady() {
        return getResponse(services -> services.allMatch(ServiceStatus::isReady));
    }

    @GET
    @PermitAll
    @Produces(APPLICATION_JSON)
    @Path("/healthy")
    public Response isHealthy() {
        return getResponse(services -> services.allMatch(ServiceStatus::isHealthy));
    }

    private Response getResponse(final Function<Stream<ServiceStatus>, Boolean> success) {

        final Map<String, ServiceStatus> servicesByStatus = getServicesByStatus();

        if (success.apply(servicesByStatus.values().stream())) {
            return Response.ok("{\"success\": true}").build();
        }

        return Response.status(SERVICE_UNAVAILABLE)
                .entity(servicesByStatus)
                .build();
    }

    Map<String, ServiceStatus> getServicesByStatus() {
        return StreamSupport
                .stream(services.spliterator(), false)
                .collect(toMap(ServiceCheck::getName, ServiceCheck::getStatus));
    }
}
