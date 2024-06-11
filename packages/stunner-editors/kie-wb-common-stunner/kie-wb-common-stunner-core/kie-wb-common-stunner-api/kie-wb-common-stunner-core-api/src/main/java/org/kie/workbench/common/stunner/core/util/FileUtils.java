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


package org.kie.workbench.common.stunner.core.util;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class FileUtils {

    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * Gets the file name without the file's extension.
     *
     * @param fullName The file with full name.
     * @return The file name without the extension.
     */
    public static String getFileNameWithoutExtension(final String fullName) {
        if (isEmpty(fullName)) {
            return fullName;
        }
        if (fullName.contains(".")) {
            return fullName.substring(0, fullName.lastIndexOf('.'));
        }

        return fullName;
    }

    /**
     * Extracts the file name from UNIX or Windows path.
     *
     * @param filePath The full file's path.
     * @return The file name including the extension.
     */
    public static String getFileName(final String filePath) {
        if (isEmpty(filePath)) {
            return filePath;
        }
        final int index = indexOfLastSeparator(filePath);
        return filePath.substring(index + 1);
    }

    private static int indexOfLastSeparator(final String filename) {
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos,
                        lastWindowsPos);
    }

}
