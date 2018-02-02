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

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;

public class Content {

    private WorkspaceProject selectedProject = null;
    private Module selectedModule = null;
    private org.guvnor.common.services.project.model.Package selectedPackage = null;
    private FolderItem selectedItem = null;
    private FolderListing folderListing = null;
    private Map<FolderItem, List<FolderItem>> siblings = new HashMap<FolderItem, List<FolderItem>>();

    public Content(final ProjectExplorerContentQuery query,
                   final WorkspaceProject project) {
        setSelectedProject(project);
        setSelectedModule(query.getModule());
        setSelectedPackage(query.getPkg());
        setSelectedItem(query.getItem());
    }

    public WorkspaceProject getSelectedProject() {
        return selectedProject;
    }

    public void setSelectedProject(final WorkspaceProject selectedProject) {
        this.selectedProject = selectedProject;
    }

    public Module getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(Module selectedModule) {
        this.selectedModule = selectedModule;
    }

    public Package getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage(Package selectedPackage) {
        this.selectedPackage = selectedPackage;
    }

    public FolderItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(FolderItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public FolderListing getFolderListing() {
        return folderListing;
    }

    public void setFolderListing(FolderListing folderListing) {
        this.folderListing = folderListing;
    }

    public Map<FolderItem, List<FolderItem>> getSiblings() {
        return siblings;
    }
}
