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

/**
 * Provides client-side exporting features.
 * <p>
 * It saves raw data into a file by producing
 * a file download dialog to appear.
 * <p>
 * Builtin content export types supported :
 * - <code>@Inject FileExport<TextContent></code>
 * - <code>@Inject FileExport<ImageDataUriContent></code>
 * - <code>@Inject FileExport<PdfDocument></code>
 * @@param T The supported content type.
 */
public interface FileExport<T> {

    /**
     * Export the <code>content</code>
     * into a file  with the name given by <code>fileName</code>
     * @param content The data to be exported
     * @param fileName The resulting file name.
     */
    public void export(T content,
                       String fileName);
}
