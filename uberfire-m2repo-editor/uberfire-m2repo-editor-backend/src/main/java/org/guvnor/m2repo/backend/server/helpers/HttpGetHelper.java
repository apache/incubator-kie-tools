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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.service.M2RepoService;

public class HttpGetHelper {

    private static final int DEFAULT_BUFFER_SIZE = 10240;
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; //1 week.
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    @Inject
    private M2RepoService m2RepoService;

    @Inject
    private GuvnorM2Repository repository;

    public void handle(final HttpServletRequest request,
                       final HttpServletResponse response,
                       final ServletContext context) throws IOException {
        String requestedFile = request.getPathInfo();

        if (requestedFile == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        requestedFile = URLDecoder.decode(requestedFile,
                                          "UTF-8");

        String repositoryName = request.getParameter("repository");

        //File traversal check:
        final File mavenRootDir = new File(repository.getM2RepositoryRootDir(repositoryName));
        final String canonicalDirPath = mavenRootDir.getCanonicalPath() + File.separator;
        final String canonicalEntryPath = new File(mavenRootDir,
                                                   requestedFile).getCanonicalPath();
        if (!canonicalEntryPath.startsWith(canonicalDirPath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        requestedFile = canonicalEntryPath.substring(canonicalDirPath.length());
        final File file = new File(mavenRootDir,
                                   requestedFile);

        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Process the ETag
        String fileName = file.getName();
        long length = file.length();
        long lastModified = file.lastModified();
        String eTag = fileName + "_" + length + "_" + lastModified;

        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && matches(ifNoneMatch,
                                           eTag)) {
            response.setHeader("ETag",
                               eTag); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
            response.setHeader("ETag",
                               eTag); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !matches(ifMatch,
                                        eTag)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        Range full = new Range(0,
                               length - 1,
                               length);
        List<Range> ranges = new ArrayList<Range>();

        String contentType = context.getMimeType(fileName);
        boolean acceptsGzip = false;
        String disposition = "inline";

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        if (contentType.startsWith("text")) {
            String acceptEncoding = request.getHeader("Accept-Encoding");
            acceptsGzip = acceptEncoding != null
                    && accepts(acceptEncoding,
                               "gzip");
            contentType += ";charset=UTF-8";
        } else if (!contentType.startsWith("image")) {
            String accept = request.getHeader("Accept");
            disposition = accept != null && accepts(accept,
                                                    contentType) ? "inline" : "attachment";
        }

        //Response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Disposition",
                           disposition +
                                   ";filename=\"" +
                                   fileName +
                                   "\"");
        response.setHeader("Accept-Ranges",
                           "bytes");
        response.setHeader("ETag",
                           eTag);
        response.setDateHeader("Last-Modified",
                               lastModified);
        response.setDateHeader("Expires",
                               System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

        RandomAccessFile input = null;
        OutputStream output = null;

        try {
            input = new RandomAccessFile(file,
                                         "r");
            output = response.getOutputStream();

            if (ranges.isEmpty() || ranges.get(0) == full) {
                Range r = full;
                response.setContentType(contentType);
                response.setHeader("Content-Range",
                                   "bytes " + r.start + "-" + r.end + "/" + r.total);

                if (acceptsGzip) {
                    response.setHeader("Content-Encoding",
                                       "gzip");
                    output = new GZIPOutputStream(output,
                                                  DEFAULT_BUFFER_SIZE);
                } else {
                    response.setHeader("Content-Length",
                                       String.valueOf(r.length));
                }

                copyRange(input,
                          output,
                          r.start,
                          r.length);
            } else if (ranges.size() == 1) {
                Range r = ranges.get(0);
                response.setContentType(contentType);
                response.setHeader("Content-Range",
                                   "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length",
                                   String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                copyRange(input,
                          output,
                          r.start,
                          r.length);
            } else {
                response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                ServletOutputStream sos = (ServletOutputStream) output;
                for (Range r : ranges) {
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY);
                    sos.println("Content-Type: " + contentType);
                    sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);

                    copyRange(input,
                              output,
                              r.start,
                              r.length);
                }

                sos.println();
                sos.println("--" + MULTIPART_BOUNDARY + "--");
            }
        } finally {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }

    private static boolean accepts(final String acceptHeader,
                                   final String toAccept) {
        String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
        Arrays.sort(acceptValues);
        return Arrays.binarySearch(acceptValues,
                                   toAccept) > -1
                || Arrays.binarySearch(acceptValues,
                                       toAccept.replaceAll("/.*$",
                                                           "/*")) > -1
                || Arrays.binarySearch(acceptValues,
                                       "*/*") > -1;
    }

    private static boolean matches(final String matchHeader,
                                   final String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues,
                                   toMatch) > -1
                || Arrays.binarySearch(matchValues,
                                       "*") > -1;
    }

    private static void copyRange(final RandomAccessFile input,
                                  final OutputStream output,
                                  final long start,
                                  final long length) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;

        if (input.length() == length) {
            // Write full range.
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer,
                             0,
                             read);
            }
        } else {
            // Write partial range.
            input.seek(start);
            long toRead = length;

            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer,
                                 0,
                                 read);
                } else {
                    output.write(buffer,
                                 0,
                                 (int) toRead + read);
                    break;
                }
            }
        }
    }

    protected class Range {

        long start;
        long end;
        long length;
        long total;

        public Range(long start,
                     long end,
                     long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }
    }
}