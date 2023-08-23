/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import elemental2.core.Global;

public class HTMLDownloadHelper {

    private static final String HEADER = "<html><body>";
    private static final String FOOTER = "</body></html>";
    private static final String FILE_EXTENSION = ".html";
    private static final String ENCODING = "data:text/html;charset=utf-8,";

    public static void downloadHTMLFile(final String filename, final String html) {
        final String sourceHTML = HEADER + html + FOOTER;
        final String source = Global.encodeURIComponent(sourceHTML);
        downloadFile(filename + FILE_EXTENSION, ENCODING + source);
    }
    public static native void downloadFile(final String fullFileName, final String encodedData) /*-{
        var aLink = document.createElement('a');
        aLink.download = fullFileName;
        aLink.href = encodedData;
        var event = new MouseEvent('click');
        aLink.dispatchEvent(event);
    }-*/;

}
