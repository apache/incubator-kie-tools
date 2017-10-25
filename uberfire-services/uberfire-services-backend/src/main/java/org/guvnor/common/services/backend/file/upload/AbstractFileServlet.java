/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.file.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.shared.file.upload.FileManagerFields;
import org.guvnor.common.services.shared.file.upload.FileOperation;
import org.jboss.errai.bus.client.api.QueueSession;
import org.jboss.errai.bus.server.api.SessionProvider;
import org.jboss.errai.bus.server.servlet.ServletBootstrapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.server.BaseFilteredServlet;

/**
 * This is for dealing with assets that have an attachment (ie assets that are really an attachment).
 */
//TODO: Basic authentication
public abstract class AbstractFileServlet extends BaseFilteredServlet {

    private static final Logger log = LoggerFactory.getLogger(AbstractFileServlet.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

    private static final long serialVersionUID = 510l;

    public static final String DEFAULT_CLIENT_ID = "0";

    protected SessionProvider sessionProvider;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        sessionProvider = ServletBootstrapUtil.getService(config).getSessionProvider();
    }

    /**
     * Load resource
     * @param path
     * @param request
     * @return
     */
    protected abstract InputStream doLoad(final Path path,
                                          final HttpServletRequest request);

    /**
     * Create a new resource
     * @param path
     * @param data
     * @param request
     * @param comment
     */
    protected abstract void doCreate(final Path path,
                                     final InputStream data,
                                     final HttpServletRequest request,
                                     final String comment);

    /**
     * Update a resource
     * @param path
     * @param data
     * @param request
     * @param comment
     */
    protected abstract void doUpdate(final Path path,
                                     final InputStream data,
                                     final HttpServletRequest request,
                                     final String comment);

    /**
     * Convert fileName and contextPath into a Path
     * @param fileName
     * @param contextPath
     * @return
     */
    protected abstract Path convertPath(final String fileName,
                                        final String contextPath) throws URISyntaxException;

    /**
     * Convert fullPath into a Path
     * @param fullPath
     * @return
     */
    protected abstract Path convertPath(final String fullPath) throws URISyntaxException;

    /**
     * Posting accepts content of various types.
     */
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        final FormData item = getFormData(request);

        if (item.getFile() != null) {
            response.getWriter().write(processUpload(item,
                                                     request,
                                                     response));
            return;
        }

        response.getWriter().write("NO-SCRIPT-DATA");
    }

    /**
     * Get the form data from the inbound request.
     */
    @SuppressWarnings("rawtypes")
    private FormData getFormData(final HttpServletRequest request) throws IOException {
        final FileItemFactory factory = new DiskFileItemFactory();
        final ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        //See https://code.google.com/p/google-web-toolkit/issues/detail?id=4682
        request.setCharacterEncoding("UTF-8");

        final FormData data = new FormData();
        try {
            final List items = upload.parseRequest(request);
            final Iterator it = items.iterator();

            FileOperation operation = null;
            String fileName = null;
            String contextPath = null;
            String fullPath = null;

            while (it.hasNext()) {
                final FileItem item = (FileItem) it.next();
                if (!item.isFormField()) {
                    data.setFile(item);
                } else if (item.getFieldName().equals(FileManagerFields.FORM_FIELD_PATH)) {
                    contextPath = item.getString("UTF-8");
                    log.debug("path:" + contextPath);
                } else if (item.getFieldName().equals(FileManagerFields.FORM_FIELD_NAME)) {
                    fileName = item.getString("UTF-8");
                    log.debug("name:" + fileName);
                } else if (item.getFieldName().equals(FileManagerFields.FORM_FIELD_FULL_PATH)) {
                    fullPath = item.getString("UTF-8");
                    log.debug("full path:" + fullPath);
                } else if (item.getFieldName().equals(FileManagerFields.FORM_FIELD_OPERATION)) {
                    operation = FileOperation.valueOf(item.getString("UTF-8"));
                    log.debug("operation:" + operation);
                }
            }

            if (operation == null) {
                throw new IllegalArgumentException("FORM_FIELD_OPERATION is null. Cannot process upload.");
            }

            org.uberfire.java.nio.file.Path path;
            switch (operation) {
                case CREATE:
                    if (fileName == null) {
                        throw new IllegalArgumentException("FORM_FIELD_NAME is null. Cannot process upload.");
                    }
                    if (contextPath == null) {
                        throw new IllegalArgumentException("FORM_FIELD_PATH is null. Cannot process upload.");
                    }
                    data.setOperation(operation);
                    data.setTargetPath(convertPath(fileName,
                                                   contextPath));
                    break;
                case UPDATE:
                    if (fullPath == null) {
                        throw new IllegalArgumentException("FORM_FIELD_FULL_PATH is null. Cannot process upload.");
                    }
                    data.setOperation(operation);
                    data.setTargetPath(convertPath(fullPath));
            }

            return data;
        } catch (Exception e) {
            throw new org.uberfire.java.nio.IOException(e.getMessage());
        }
    }

    private String processUpload(final FormData item,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response) throws IOException {

        // If the file it doesn't exist.
        if ("".equals(item.getFile().getName())) {
            throw new IOException("No file selected.");
        }

        final String processResult = uploadFile(item,
                                                request,
                                                response);

        return processResult;
    }

    private String uploadFile(final FormData item,
                              final HttpServletRequest request,
                              final HttpServletResponse response) throws IOException {
        final InputStream fileData = item.getFile().getInputStream();
        final org.uberfire.backend.vfs.Path targetPath = item.getTargetPath();

        if (!validateAccess(Paths.convert(targetPath),
                            response)) {
            return "FAIL";
        }

        try {
            switch (item.getOperation()) {
                case CREATE:
                    doCreate(targetPath,
                             fileData,
                             request,
                             "Uploaded " + getTimestamp());
                    break;
                case UPDATE:
                    doUpdate(targetPath,
                             fileData,
                             request,
                             "Uploaded " + getTimestamp());
            }
        } finally {
            item.getFile().getInputStream().close();
        }

        return "OK";
    }

    private String getTimestamp() {
        final Calendar now = Calendar.getInstance();
        final StringBuilder sb = new StringBuilder();
        sb.append(sdf.format(now.getTime()));
        return sb.toString();
    }

    /**
     * doGet acting like a dispatcher.
     */
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        final String path = request.getParameter(FileManagerFields.FORM_FIELD_PATH);

        if (path != null) {
            processAttachmentDownload(path,
                                      request,
                                      response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void processAttachmentDownload(final String url,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            final Path sourcePath = convertPath(url);

            if (!validateAccess(Paths.convert(sourcePath),
                                response)) {
                return;
            }

            IOUtils.copy(doLoad(sourcePath,
                                request),
                         output);
            //Use the encoded form from in the URL (rather than encode/decode for fun!)
            //See http://tools.ietf.org/html/rfc6266 for details of filename* content-disposition usage
            final String fileName = url.substring(url.lastIndexOf("/") + 1);

            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition",
                               "attachment; filename*=utf-8''" + fileName);

            response.setContentLength(output.size());
            response.getOutputStream().write(output.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new org.uberfire.java.nio.IOException(e.getMessage());
        }
    }

    protected String getSessionId(final HttpServletRequest request,
                                  final SessionProvider sessionProvider) {
        final HttpSession session = request.getSession(true);
        final String clientId = getClientId(request);
        final QueueSession queueSession = sessionProvider.createOrGetSession(session,
                                                                             clientId);

        return queueSession.getSessionId();
    }

    private String getClientId(final HttpServletRequest request) {
        String clientId = request.getParameter("clientId");

        if (clientId == null) {
            log.warn("Parameter named 'clientId' should be not null!");
            clientId = DEFAULT_CLIENT_ID;
        }

        return clientId;
    }
}
