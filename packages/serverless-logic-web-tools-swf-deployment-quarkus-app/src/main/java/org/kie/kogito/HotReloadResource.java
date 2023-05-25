/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.kie.kogito.api.UploadService;
import org.kie.kogito.model.UploadException;
import org.kie.kogito.model.ValidationException;

@Path("/")
public class HotReloadResource {

    private static final Logger LOGGER = Logger.getLogger(HotReloadResource.class);

    private static final String DATA_PART_KEY = "zipFile";

    private static final String ERRORS_KEY = "errors";
    private static final String PATHS_KEY = "paths";

    @Inject
    UploadService uploadService;

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response handleUpload(@MultipartForm MultipartFormDataInput dataInput) {
        try {
            var inputStream = dataInput.getFormDataPart(DATA_PART_KEY, InputStream.class, null);

            if (inputStream == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No file part found").build();
            }

            final List<String> validPaths = uploadService.upload(inputStream);

            final Map<String, List<String>> pathMap = new HashMap<>();
            pathMap.put(PATHS_KEY, validPaths);

            return Response.ok(pathMap, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (ValidationException e) {
            LOGGER.warn(e.getMessage());

            final Map<String, List<String>> errorsMap = new HashMap<>();
            errorsMap.put(ERRORS_KEY, e.getErrorMessages());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorsMap)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (UploadException e) {
            LOGGER.warn(e.getMessage());

            final Map<String, List<String>> errorsMap = new HashMap<>();
            errorsMap.put(ERRORS_KEY, Collections.singletonList(e.getMessage()));

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorsMap)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Something went wrong", e);

            final Map<String, List<String>> errorsMap = new HashMap<>();
            errorsMap.put(ERRORS_KEY, Collections.singletonList(e.getMessage()));

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorsMap)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        }
    }
}
