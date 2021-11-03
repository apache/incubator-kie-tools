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

import static org.dashbuilder.backend.RuntimeOptions.DASHBOARD_EXTENSION;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource to receive new imports.
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

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@MultipartForm FileUploadModel form) throws IOException {
        if (!runtimeOptions.isAllowUpload() && !runtimeModelRegistry.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("New uploads are disabled.").build();
        }
        var inputBytes = form.fileData;

        checkInputSize(inputBytes);

        var dashboardOp = checkForExistingFile(inputBytes);
        if (dashboardOp.isPresent()) {
            var dashboardName = dashboardOp.get();
            logger.info("Found existing file with same contents: {}", dashboardName);
            if (runtimeModelRegistry.get(dashboardName).isPresent()) {
                return Response.status(Status.CONFLICT).entity(dashboardName).build();
            } else {
                return registerExistingFile(dashboardName);
            }
        }
        return registerNewFile(form.fileName, inputBytes);
    }

    /**
     * Reads the uploaded model bytes controlling the size and throwing exception when the size exceeds the allowed size.
     * @param fileData
     * @return
     */
    private void checkInputSize(byte[] bytes) {
        if (bytes.length > runtimeOptions.getUploadSize()) {
            logger.debug("Total size {} is greater than the allowed size {}",
                         bytes.length,
                         runtimeOptions.getUploadSize());
            throw new WebApplicationException("Upload size is greater than the allowed size: " + runtimeOptions.getUploadSize(),
                                              Response.Status.BAD_REQUEST);
        }
    }

    /**
     * 
     * If a file exists with a given size then probably it is a repeated.
     * 
     * @param uploadSize
     * @return
     * @throws IOException 
     */
    private Optional<String> checkForExistingFile(byte[] uploadedFile) throws IOException {
        try (var walk = Files.walk(Paths.get(runtimeOptions.getImportsBaseDir()), 1)) {
            return walk
                       .filter(p -> p.toFile().isFile() &&
                                    p.toString().toLowerCase().endsWith(DASHBOARD_EXTENSION) &&
                                    isContentEquals(uploadedFile, p))
                       .map(p -> p.getFileName().toString().replace(DASHBOARD_EXTENSION, ""))
                       .findFirst();

        } catch (Exception e) {
            logger.info("Error checking for duplicated file contents.");
            logger.debug("Error checking for duplicated file contents.", e);
            return Optional.empty();
        }

    }

    private boolean isContentEquals(byte[] uploadedFile, java.nio.file.Path p) {
        try (var fis = Files.newInputStream(p)) {
            return IOUtils.contentEquals(fis, new ByteArrayInputStream(uploadedFile));
        } catch (IOException e) {
            logger.debug("Error checking file {}. Skipping from verification.", p, e);
        }
        return false;
    }

    private Response registerNewFile(String fileName, byte[] inputBytes) throws IOException {
        var newImportInfo = runtimeOptions.newFilePath(fileName);
        var path = Paths.get(newImportInfo.getPath());
        Files.write(path, inputBytes);

        try {
            runtimeModelRegistry.registerFile(newImportInfo.getPath());
        } catch (Exception e) {
            Files.delete(path);
            logger.error("Error uploading file", e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(e.getMessage())
                           .build();
        }

        return Response.ok(newImportInfo.getImportId()).build();
    }

    private Response registerExistingFile(String dashboardName) {
        var filePath = runtimeOptions.buildFilePath(dashboardName);
        runtimeModelRegistry.registerFile(filePath);
        return Response.ok(dashboardName).build();
    }

}