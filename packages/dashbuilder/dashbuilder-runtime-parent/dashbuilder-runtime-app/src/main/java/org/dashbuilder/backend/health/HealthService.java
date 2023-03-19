/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.health;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Service that reports if Runtime is ready and healthy.
 *
 */
@Path("/")
public class HealthService {

    private static final String SUCCESS_RESPONSE = "{\"success\": true}";

    @GET
    @PermitAll
    @Path("/ready")
    @Produces(APPLICATION_JSON)
    public String ready() {
        return SUCCESS_RESPONSE;
    }

    @GET
    @PermitAll
    @Path("/healthy")
    @Produces(APPLICATION_JSON)
    public String alive() {
        return SUCCESS_RESPONSE;
    }

}