/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.model;

import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ProjectExplorerContent {

    private WorkspaceProject project;
    private Module module;

    private FolderListing folderListing;
    private Map<FolderItem, List<FolderItem>> siblings;

    public ProjectExplorerContent() {
    }

    public ProjectExplorerContent(final WorkspaceProject project,
                                  final Module module,
                                  final FolderListing folderListing,
                                  final Map<FolderItem, List<FolderItem>> siblings) {
        this.project = project;
        this.module = module;
        this.folderListing = folderListing;
        this.siblings = siblings;
    }

    public WorkspaceProject getProject() {
        return project;
    }

    public Module getModule() {
        return module;
    }

    public FolderListing getFolderListing() {
        return folderListing;
    }

    public Map<FolderItem, List<FolderItem>> getSiblings() {
        return siblings;
    }
}
