/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scorecardxls.client.editor;

import com.google.gwt.core.client.GWT;
import org.guvnor.common.services.shared.file.upload.FileManagerFields;
import org.uberfire.backend.vfs.Path;
import org.uberfire.util.URIUtil;

/**
 * Utility to get the Servlet URL
 */
public class URLHelper {

    private static final String BASE = "scorecardxls/file";

    public static String getServletUrl( final String clientId ) {
        return getServletUrl() + "?clientId=" + clientId;
    }

    public static String getDownloadUrl( final Path path,
                                         final String clientId ) {
        return URLHelper.getServletUrl(clientId) + "&" + FileManagerFields.FORM_FIELD_PATH + "=" + URIUtil.encodeQueryString(path.toURI());
    }

    private static String getServletUrl() {
        return GWT.getModuleBaseURL() + BASE;
    }
}
