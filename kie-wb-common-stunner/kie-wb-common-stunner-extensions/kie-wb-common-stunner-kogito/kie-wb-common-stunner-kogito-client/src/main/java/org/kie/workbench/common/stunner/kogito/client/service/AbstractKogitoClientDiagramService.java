/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.service;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public abstract class AbstractKogitoClientDiagramService implements KogitoClientDiagramService {

    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    public static final String DEFAULT_DIAGRAM_ID = "default";

    /**
     * Making correct ID diagram from path:
     * 1. Extracts file name without extension from path
     * 2. Returns {@link AbstractKogitoClientDiagramService#generateDefaultId}
     * If name is empty (can be overridden in descendant)
     * @param filePath path to the file
     * @return file name
     */
    public String createDiagramTitleFromFilePath(final String filePath) {
        if (isEmpty(filePath)) {
            return generateDefaultId();
        }

        return getFileNameWithoutExtension(getFileName(filePath));
    }

    private static String getFileNameWithoutExtension(final String fullName) {
        if (fullName.contains(".")) {
            return fullName.substring(0, fullName.lastIndexOf('.'));
        }

        return fullName;
    }

    private static String getFileName(final String filePath) {
        final int index = indexOfLastSeparator(filePath);
        return filePath.substring(index + 1);
    }

    private static int indexOfLastSeparator(final String filename) {
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos,
                        lastWindowsPos);
    }

    public String generateDefaultId() {
        return DEFAULT_DIAGRAM_ID;
    }
}
