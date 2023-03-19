/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.backend.resources;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.dashbuilder.shared.service.RuntimeModelService;

@ApplicationScoped
@Path("runtime-model")
@Produces(MediaType.APPLICATION_JSON)
public class RuntimeModelResource {

    @Inject
    RuntimeModelService runtimeModelService;

    @GET
    @Path("info")
    public Response info(@QueryParam("modelId") String modelId) {
        return Optional.ofNullable(runtimeModelService.info(modelId))
                       .map(Response::ok)
                       .orElse(Response.status(Status.NOT_FOUND))
                       .build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String runtimeModelId) {
        return runtimeModelService.getRuntimeModel(runtimeModelId)
                                  .map(Response::ok)
                                  .orElse(Response.status(Status.NOT_FOUND))
                                  .build();
    }

}