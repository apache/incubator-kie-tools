/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.explorer.service;

import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.uberfire.backend.vfs.Path;

/**
 * Service definition for Explorer editor
 */
@Remote
public interface ExplorerService {

    String BUILD_PROJECT_PROPERTY_NAME = "org.kie.build.disable-project-explorer";

    ProjectExplorerContent getContent(final String path,
                                      final ActiveOptions activeOptions);

    ProjectExplorerContent getContent(final ProjectExplorerContentQuery query);

    FolderListing getFolderListing(final WorkspaceProject project,
                                   final Module module,
                                   final FolderItem item,
                                   final ActiveOptions options);

    Package resolvePackage(final FolderItem item);

    Set<Option> getLastUserOptions();

    void deleteItem(final FolderItem folderItem,
                    final String comment);

    void renameItem(final FolderItem folderItem,
                    final String newFileName,
                    final String commitMessage);

    void copyItem(final FolderItem folderItem,
                  final String newFileName,
                  final Path targetDirectory,
                  final String commitMessage);

    WorkspaceProject resolveProject(final String path);
}
