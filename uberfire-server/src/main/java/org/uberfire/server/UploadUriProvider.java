/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.server;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.uberfire.util.URIUtil;

import static org.uberfire.server.util.FileServletUtil.decodeFileNamePart;
import static org.uberfire.server.util.FileServletUtil.encodeFileName;
import static org.uberfire.server.util.FileServletUtil.encodeFileNamePart;

public class UploadUriProvider {

    private static final String PARAM_PATH = "path";
    private static final String PARAM_FOLDER = "folder";
    private static final String PARAM_FILENAME = "fileName";

    public static URI getTargetLocation(final HttpServletRequest request) throws URISyntaxException,
            FileUploadException {

        if (request.getParameter(PARAM_PATH) != null) {
            String parameter = request.getParameter(PARAM_PATH);
            String decode = decodeFileNamePart(parameter);
            String str = encodeFileNamePart(decode);
            return new URI(str);
        } else if (request.getParameter(PARAM_FOLDER) != null) {
            return new URI(request.getParameter(PARAM_FOLDER).replaceAll("\\s", "%20") + "/" + encodeFileName(URIUtil.decode(request.getParameter(PARAM_FILENAME))));
        } else {
            throw new FileUploadException("Path to file was invalid.");
        }
    }
}
