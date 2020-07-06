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

package org.dashbuilder.backend.resources.api;

import java.io.IOException;

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

import org.dashbuilder.backend.resources.FileUploadModel;
import org.dashbuilder.backend.resources.UploadResourceImpl;
import org.dashbuilder.backend.services.RuntimeInfoService;
import org.dashbuilder.shared.model.DashbuilderRuntimeInfo;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("api/")
@Produces(MediaType.APPLICATION_JSON)
public class DashbuilderRuntimeResource {

    private static final String DASHBOARD_BASE_URI = "dashboard";
    private static final String DASHBOARD_ID_URI = DASHBOARD_BASE_URI + "/{id}";

    @Inject
    RuntimeInfoService runtimeInfoService;

    @Inject
    UploadResourceImpl uploadResourceImpl;

    @GET
    public DashbuilderRuntimeInfo info() {
        return runtimeInfoService.info();
    }

    @GET
    @Path(DASHBOARD_ID_URI)
    public Response dashboard(@PathParam("id") String id) {
        return runtimeInfoService.dashboardInfo(id)
                                 .map(info -> Response.ok().entity(info).build())
                                 .orElse(Response.status(Status.NOT_FOUND).build());
    }

    @POST
    @Path(DASHBOARD_BASE_URI)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadResource(@MultipartForm FileUploadModel form) throws IOException {
        return uploadResourceImpl.uploadFile(form);
    }

}