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

package org.kie.workbench.common.dmn.api;

import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.uberfire.backend.vfs.Path;

@Remote
public interface DMNContentService {

    /**
     * Returns the content of a file as string.
     * @param path The file path.
     * @return a string with the content of file.
     */
    String getContent(final Path path);

    /**
     * Returns the content as string and the metadata of a file in a project.
     * @param path The file path.
     * @param defSetId The diagram's and graph  Definition Set identifier, persisted into the metadata.
     * @return a {@link DMNContentResource} instance with the file content and the metadata
     */
    DMNContentResource getProjectContent(final Path path,
                                         final String defSetId);

    /**
     * Save the content as string and the metadata into the specified path.
     * @param path The file path.
     * @param content The content of the file.
     * @param metadata The metadata of the file.
     * @param comment The commit message.
     */
    void saveContent(final Path path,
                     final String content,
                     final Metadata metadata,
                     final String comment);

    /**
     * This method loads all paths (DMN and PMML) from a given project.
     * @param workspaceProject represents the project that will be scanned.
     * @return all {@link Path}s from a given project.
     */
    List<Path> getModelsPaths(final WorkspaceProject workspaceProject);

    /**
     * This method loads all DMN paths from a given project.
     * @param workspaceProject represents the project that will be scanned.
     * @return all DMN {@link Path}s from a given project.
     */
    List<Path> getDMNModelsPaths(final WorkspaceProject workspaceProject);

    /**
     * This method loads all PMML paths from a given project.
     * @param workspaceProject represents the project that will be scanned.
     * @return all PMML {@link Path}s from a given project.
     */
    List<Path> getPMMLModelsPaths(final WorkspaceProject workspaceProject);

    /**
     * This method loads a {@link PMMLDocumentMetadata} for a given path.
     * @param path represents the path of the PMML model that will be loaded as a {@link PMMLDocumentMetadata}.
     * @return the {@link PMMLDocumentMetadata} for a given path.
     */
    PMMLDocumentMetadata loadPMMLDocumentMetadata(final Path path);
}
