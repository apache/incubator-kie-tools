/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.kie.kogito.model.UploadStatus;
import org.kie.kogito.service.UploadService;

@Path("/")
public class DmnDevDeploymentResource {

    private static final String DATA_PART_KEY = "zipFile";

    @Inject
    UploadService uploadService;

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response handleUpload(@MultipartForm MultipartFormDataInput dataInput) throws IOException {
        if (uploadService.getStatus() != UploadStatus.WAITING) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Upload cannot be done anymore").build();
        }

        var inputStream = dataInput.getFormDataPart(DATA_PART_KEY, InputStream.class, null);

        if (inputStream == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No file part found").build();
        }

        uploadService.upload(inputStream);

        return Response.ok().build();
    }

    @GET
    @Path("upload/status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadStatus() {
        return Response.ok(uploadService.getStatus()).build();
    }
}