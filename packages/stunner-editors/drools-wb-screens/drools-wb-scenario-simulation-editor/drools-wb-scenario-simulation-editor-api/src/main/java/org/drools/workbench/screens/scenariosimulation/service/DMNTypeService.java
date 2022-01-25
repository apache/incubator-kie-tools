/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.service;

import org.drools.scenariosimulation.api.model.Settings;
import org.drools.workbench.screens.scenariosimulation.model.DMNMetadata;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.uberfire.backend.vfs.Path;

public interface DMNTypeService {

    /**
     * Retrieves a <code>FactModelTuple</code> representing the given <b>dmn</b> file
     * @param path
     * @param dmnPath
     * @return
     * @throws Exception
     */
    FactModelTuple retrieveFactModelTuple(Path path, String dmnPath);

    /**
     * Extract name and namespace from DMN model (dmnPath) and update settings
     * @param settings to update
     * @param path to project
     * @param dmnPath to dmn file
     */
    void initializeNameAndNamespace(Settings settings, Path path, String dmnPath);

    /**
     * It returns the DMN name and namespace given a dmnPath
     * @param path to project
     * @param dmnPath to dmn file
     */
    DMNMetadata getDMNMetadata(Path path, String dmnPath);
}
