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

package org.kie.workbench.common.widgets.configresource.client.widget;

import org.kie.soup.project.datamodel.imports.Import;

public class BuiltInTypeImportHelper {

    private BuiltInTypeImportHelper() {
        // Suggested by Sonar Cloud
    }

    /**
     * Check if the import is java built in type ("java.lang.*" or "java.util.*")
     * @return false if type is from listed packages, true otherwise
     */
    public static boolean isBuiltIn(final Import importedType) {
        final String type = importedType.getType();
        return isBuiltIn(type);
    }

    /**
     * Check if the given type is java built in type ("java.lang.*" or "java.util.*")
     * @return false if type is from listed packages, true otherwise
     */
    public static boolean isBuiltIn(final String type) {
        return type != null && (type.startsWith("java.lang.") || type.startsWith("java.util."));
    }
}
