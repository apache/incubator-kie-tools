/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.backend.archive;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guvnor.common.services.shared.file.upload.FileManagerFields;
import org.uberfire.server.BaseFilteredServlet;

public class ArchiveServlet
        extends BaseFilteredServlet {

    @Inject
    private Archiver archiver;

    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        final String uri = request.getParameter(FileManagerFields.FORM_FIELD_PATH);

        try {
            if (uri != null) {

                if (!validateAccess(new URI(uri),
                                    response)) {
                    return;
                }

                // Try to extract a meaningful name for the zip-file from the URI.
                int index = uri.lastIndexOf("@") + 1;
                if (index < 0) {
                    index = 0;
                }
                String downLoadFileName = uri.substring(index);
                if (downLoadFileName.startsWith("/")) {
                    downLoadFileName = downLoadFileName.substring(1);
                }
                if (downLoadFileName.endsWith("/")) {
                    downLoadFileName = downLoadFileName.substring(0,
                                                                  downLoadFileName.length() - 1);
                }
                downLoadFileName = downLoadFileName.replaceAll("/",
                                                               "_");

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                archiver.archive(outputStream,
                                 uri);

                response.setContentType("application/zip");
                response.setHeader("Content-Disposition",
                                   "attachment; filename=" + downLoadFileName + ".zip");

                response.setContentLength(outputStream.size());
                response.getOutputStream().write(outputStream.toByteArray());
                response.getOutputStream().flush();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (URISyntaxException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
