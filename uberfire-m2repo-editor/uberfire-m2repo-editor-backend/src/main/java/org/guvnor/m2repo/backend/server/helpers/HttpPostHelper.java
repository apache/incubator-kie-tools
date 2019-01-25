/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.m2repo.backend.server.helpers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.appformer.maven.support.PomModel;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.guvnor.m2repo.model.HTMLFileManagerFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_MISSING_POM;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_OK;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_UNABLE_TO_PARSE_POM;
import static org.guvnor.m2repo.utils.FileNameUtilities.isJar;
import static org.guvnor.m2repo.utils.FileNameUtilities.isKJar;
import static org.guvnor.m2repo.utils.FileNameUtilities.isPom;

public class HttpPostHelper {

    private static final Logger log = LoggerFactory.getLogger(HttpPostHelper.class);

    @Inject
    protected ExtendedM2RepoService m2RepoService;

    /**
     * Posting accepts content of various types -
     * may be an attachment for an asset, or perhaps a repository import to process.
     */
    public void handle(final HttpServletRequest request,
                       final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        final FormData formData = extractFormData(request);
        final String result = upload(formData);
        response.getWriter().write(result);
    }

    @SuppressWarnings("rawtypes")
    protected FormData extractFormData(final HttpServletRequest request) throws IOException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        FormData data = new FormData();
        GAV emptyGAV = new GAV();
        try {
            List items = upload.parseRequest(request);
            Iterator it = items.iterator();
            while (it.hasNext()) {
                FileItem item = (FileItem) it.next();
                if (!item.isFormField()) {
                    data.setFile(item);
                }

                if (item.isFormField() && item.getFieldName().equals(HTMLFileManagerFields.GROUP_ID)) {
                    emptyGAV.setGroupId(item.getString());
                } else if (item.isFormField() && item.getFieldName().equals(HTMLFileManagerFields.ARTIFACT_ID)) {
                    emptyGAV.setArtifactId(item.getString());
                } else if (item.isFormField() && item.getFieldName().equals(HTMLFileManagerFields.VERSION_ID)) {
                    emptyGAV.setVersion(item.getString());
                }
            }

            if (isNullOrEmpty(emptyGAV.getGroupId())
                    || isNullOrEmpty(emptyGAV.getArtifactId())
                    || isNullOrEmpty(emptyGAV.getVersion())) {
                data.setGav(null);
            } else {
                data.setGav(emptyGAV);
            }

            return data;
        } catch (FileUploadException e) {
            log.error(e.getMessage(),
                      e);
        }

        return null;
    }

    protected String upload(final FormData formData) throws IOException {
        //Validate upload
        final FileItem fileItem = formData.getFile();
        if (fileItem == null) {
            throw new IOException("No file selected.");
        }
        final String fileName = fileItem.getName();
        if (isNullOrEmpty(fileName)) {
            throw new IOException("No file selected.");
        }

        if (isJar(fileName) || isKJar(fileName)) {
            return uploadJar(formData);
        } else if (isPom(fileName)) {
            return uploadPom(formData);
        } else {
            throw new IOException("Unsupported file type selected.");
        }
    }

    protected String uploadJar(final FormData formData) throws IOException {
        GAV gav = formData.getGav();
        InputStream jarStream = null;

        try {
            jarStream = formData.getFile().getInputStream();
            if (gav == null) {
                if (!jarStream.markSupported()) {
                    jarStream = new BufferedInputStream(jarStream);
                }

                // is available() safe?
                jarStream.mark(jarStream.available());

                PomModel pomModel = PomModelResolver.resolveFromJar(jarStream);

                //If we were able to get a POM model we can get the GAV
                if (pomModel != null) {
                    String groupId = pomModel.getReleaseId().getGroupId();
                    String artifactId = pomModel.getReleaseId().getArtifactId();
                    String version = pomModel.getReleaseId().getVersion();

                    if (isNullOrEmpty(groupId) || isNullOrEmpty(artifactId) || isNullOrEmpty(version)) {
                        return UPLOAD_MISSING_POM;
                    } else {
                        gav = new GAV(groupId,
                                      artifactId,
                                      version);
                    }
                } else {
                    return UPLOAD_MISSING_POM;
                }
                jarStream.reset();
                
                formData.setGav(gav);
            }
            deploy(gav, jarStream);

            return UPLOAD_OK;
        } catch (IOException ioe) {
            log.error(ioe.getMessage(),
                      ioe);
            throw ExceptionUtilities.handleException(ioe);
        } finally {
            if (jarStream != null) {
                jarStream.close();
            }
        }
    }
    
    protected void deploy(GAV gav, InputStream jarStream) {

        m2RepoService.deployJar(jarStream,
                                gav);
    }

    protected String uploadPom(final FormData formData) throws IOException {
        ReusableInputStream pomStream = null;

        try {
            GAV gav;
            pomStream = new ReusableInputStream(formData.getFile().getInputStream());

            // is available() safe?
            pomStream.mark(pomStream.available());

            try {
                final PomModel pomModel = PomModelResolver.resolveFromPom(pomStream);
                final String groupId = pomModel.getReleaseId().getGroupId();
                final String artifactId = pomModel.getReleaseId().getArtifactId();
                final String version = pomModel.getReleaseId().getVersion();

                if (isNullOrEmpty(groupId) || isNullOrEmpty(artifactId) || isNullOrEmpty(version)) {
                    return UPLOAD_UNABLE_TO_PARSE_POM;
                } else {
                    gav = new GAV(groupId,
                                  artifactId,
                                  version);
                }
            } catch (Exception e) {
                log.error("Could not parse the uploaded POM.XML file.",
                          e);
                return UPLOAD_UNABLE_TO_PARSE_POM;
            }
            pomStream.reset();

            m2RepoService.deployPom(pomStream,
                                    gav);

            return UPLOAD_OK;
        } catch (IOException ioe) {
            log.error(ioe.getMessage(),
                      ioe);
            throw ExceptionUtilities.handleException(ioe);
        } finally {
            if (pomStream != null) {
                pomStream.doClose();
            }
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * PomModelResolver uses org.kie.scanner.embedder.MavenEmbedder which closes the underlying
     * InputStream once the PomModel has been resolved. We however need to keep the InputStream
     * open to be able to write the file to GuvnorM2Repository. Therefore this sub-class
     * overrides close to prevent the InputStream from being closed prematurely.
     */
    private static class ReusableInputStream extends BufferedInputStream {

        public ReusableInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            //Do nothing.
        }

        void doClose() throws IOException {
            //Do the closure
            super.close();
        }
    }
}
