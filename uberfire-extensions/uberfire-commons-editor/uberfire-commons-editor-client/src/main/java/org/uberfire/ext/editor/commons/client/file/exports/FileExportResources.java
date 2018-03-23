/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.exports;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Static resources related to the file export.
 */
public interface FileExportResources extends ClientBundle {

    FileExportResources INSTANCE = GWT.create(FileExportResources.class);

    // The File Saver js.
    @Source("js/FileSaver.min.js")
    TextResource fileSaver();

    // The jsPDF js.
    @Source("js/jspdf.min.js")
    TextResource jsPdf();

    @Source("js/canvas2svg.js")
    TextResource canvas2svg();
}
