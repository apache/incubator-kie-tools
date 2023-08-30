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


package org.kie.workbench.common.stunner.kogito.client.service;

import static org.kie.workbench.common.stunner.core.util.FileUtils.getFileName;
import static org.kie.workbench.common.stunner.core.util.FileUtils.getFileNameWithoutExtension;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public abstract class AbstractKogitoClientDiagramService implements KogitoClientDiagramService {

    public static final String DEFAULT_DIAGRAM_ID = "default";

    /**
     * Making correct ID diagram from path:
     * 1. Extracts file name without extension from path
     * 2. Returns {@link AbstractKogitoClientDiagramService#generateDefaultId}
     * If name is empty (can be overridden in descendant)
     *
     * @param filePath path to the file
     * @return file name
     */
    public String createDiagramTitleFromFilePath(final String filePath) {
        if (isEmpty(filePath)) {
            return generateDefaultId();
        }

        return getFileNameWithoutExtension(getFileName(filePath));
    }

    public String generateDefaultId() {
        return DEFAULT_DIAGRAM_ID;
    }
}
