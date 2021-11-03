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
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;

/**
 * Data Set services exposed as REST Web Service
 *
 */
@Path("/dataset")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataSetResource {

    @Inject
    DataSetManager manager;

    @Inject
    DataSetDefRegistry dataSetDefRegistry;

    @POST
    @Path("lookup")
    public Response lookupDataSet(DataSetLookup lookup) {
        return checkError(() -> getDataSetDef(lookup).map(def -> Response.ok(manager.lookupDataSet(lookup)))
                                                     .orElse(Response.status(Status.NOT_FOUND))
                                                     .build());
    }

    @GET
    @Path("{uuid}/metadata")
    public Response lookupDataSetMetadata(@PathParam("uuid") String uuid) {
        return checkError(() -> getDataSetDef(uuid).map(def -> Response.ok(manager.getDataSetMetadata(uuid)))
                                                   .orElse(Response.status(Status.NOT_FOUND))
                                                   .build());
    }

    private Response checkError(Supplier<Response> execution) {
        try {
            return execution.get();
        } catch (Exception e) {
            return error(e);
        }
    }

    private Optional<DataSetDef> getDataSetDef(DataSetLookup lookup) {
        return getDataSetDef(lookup.getDataSetUUID());
    }

    private Optional<DataSetDef> getDataSetDef(String uuid) {
        return Optional.ofNullable(dataSetDefRegistry.getDataSetDef(uuid));
    }

    private Response error(Exception e) {
        var message = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
        return Response.serverError().entity(message).build();
    }

}