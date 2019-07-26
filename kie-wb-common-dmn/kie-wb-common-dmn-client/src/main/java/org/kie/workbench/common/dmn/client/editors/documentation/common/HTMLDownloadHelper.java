/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import elemental2.core.Global;
import elemental2.dom.HTMLAnchorElement;

import static elemental2.dom.DomGlobal.document;

public class HTMLDownloadHelper {

    private final static String HEADER = "<html><body>";
    private final static String FOOTER = "</body></html>";
    private final static String FILE_EXTENSION = ".HTML";
    private final static String ENCODING = "data:text/html;charset=utf-8,";

    public void download(final String filename, final String html) {

        final String sourceHTML = HEADER + html + FOOTER;
        final String source = Global.encodeURIComponent(sourceHTML);

        final HTMLAnchorElement fileDownload = (HTMLAnchorElement) document.createElement("a");
        document.body.appendChild(fileDownload);
        fileDownload.href = ENCODING + source;
        fileDownload.download = filename + FILE_EXTENSION;
        fileDownload.click();
        document.body.removeChild(fileDownload);
    }
}
