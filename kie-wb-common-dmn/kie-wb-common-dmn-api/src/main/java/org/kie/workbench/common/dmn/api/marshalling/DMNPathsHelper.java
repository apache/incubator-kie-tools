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

package org.kie.workbench.common.dmn.api.marshalling;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.uberfire.backend.vfs.Path;

/**
 * This helper provides methods to handle common path operations in the DMN context.
 */
public interface DMNPathsHelper {

    /**
     * This method returns all model paths (DMN and PMML) for a given project.
     * @param workspaceProject represents the project that will be scanned.
     * @return all paths from a given project.
     */
    List<Path> getModelsPaths(final WorkspaceProject workspaceProject);

    /**
     * This method returns all DMN paths for a given project.
     * @param workspaceProject represents the project that will be scanned.
     * @return all paths from a given project.
     */
    List<Path> getDMNModelsPaths(final WorkspaceProject workspaceProject);

    /**
     * This method returns all PMML paths for a given project.
     * @param workspaceProject represents the project that will be scanned.
     * @return all paths from a given project.
     */
    List<Path> getPMMLModelsPaths(final WorkspaceProject workspaceProject);

    /**
     * Returns a {@link String} representation of the relative {@link Path} between two other {@link Path}s.
     * @param dmnModelPath The {@link Path} of the DMN file being edited.
     * @param includedModelPath The {@link Path} of an included external model file.
     * @return
     */
    String getRelativeURI(final Path dmnModelPath,
                          final Path includedModelPath);
}
