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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.data.Pair;

/**
 * Resource to receive new imports
 *
 */
@Path("/upload")
@ApplicationScoped
public class UploadResourceImpl {

    Logger logger = LoggerFactory.getLogger(UploadResourceImpl.class);

    @Inject
    RuntimeOptions runtimeOptions;

    @Inject
    RuntimeModelRegistry runtimeModelRegistry;

    @PostConstruct
    public void createBaseDir() {
        java.nio.file.Path baseDirPath = Paths.get(runtimeOptions.getImportsBaseDir());
        if (!baseDirPath.toFile().exists()) {
            try {
                Files.createDirectory(baseDirPath);
            } catch (IOException e) {
                logger.debug("Error creating base directory for imports: {}", baseDirPath, e);
                throw new RuntimeException("Base directory for imports could not be created: " + baseDirPath, e);
            }
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@MultipartForm FileUploadModel form) throws IOException {
        int uploadSize = form.getFileData().length;

        if (uploadSize > runtimeOptions.getUploadSize()) {
            logger.error("Stopping upload of size {}. Max size is {}", uploadSize, runtimeOptions.getUploadSize());
            return Response.status(Response.Status.BAD_REQUEST).entity("Upload is too big.").build();
        }

        logger.info("Uploading file with size {} bytes", form.getFileData().length);

        Pair<String, String> newImportInfo = runtimeOptions.newFilePath();
        java.nio.file.Path path = Paths.get(newImportInfo.getK2());

        Files.write(path, form.getFileData());

        try {
            runtimeModelRegistry.registerFile(newImportInfo.getK2());
        } catch (Exception e) {
            Files.delete(path);
            logger.error("Error uploading file", e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(e.getMessage())
                           .build();
        }

        return Response.ok(newImportInfo.getK1()).build();
    }

}