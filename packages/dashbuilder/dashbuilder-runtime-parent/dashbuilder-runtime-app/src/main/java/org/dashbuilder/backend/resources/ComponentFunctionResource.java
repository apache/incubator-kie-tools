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
package org.dashbuilder.backend.resources;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dashbuilder.external.service.BackendComponentFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("function")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComponentFunctionResource {

    Logger logger = LoggerFactory.getLogger(ComponentFunctionResource.class);

    @Inject
    BackendComponentFunctionService backendComponentFunctionService;

    @GET
    public Response list() {
        return Response.ok(backendComponentFunctionService.listFunctions()).build();
    }

    @POST
    @Path("{name}")
    public Response invoke(@PathParam("name") String name, Map<String, Object> params) {
        try {
            var result = backendComponentFunctionService.callFunction(name, params);
            return Response.ok(result).build();
        } catch (Exception e) {
            logger.debug("Error invoking component function", e);
            logger.info("Error invoking component function: {}", e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}
