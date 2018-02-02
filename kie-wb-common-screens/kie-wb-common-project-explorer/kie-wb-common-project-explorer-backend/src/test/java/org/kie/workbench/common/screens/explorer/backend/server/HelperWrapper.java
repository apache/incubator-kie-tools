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

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class HelperWrapper {

    private ArgumentCaptor<WorkspaceProject> projectArgumentCaptor;
    private ArgumentCaptor<Module> moduleArgumentCaptor;
    private ArgumentCaptor<FolderListing> folderListingArgumentCaptor;
    private ArgumentCaptor<Package> packageArgumentCaptor;
    private ArgumentCaptor<ActiveOptions> activeOptionsArgumentCaptor;
    private ExplorerServiceHelper helper;
    private boolean includePackage = true;

    public HelperWrapper(ExplorerServiceHelper helper) {
        this.helper = helper;
    }

    public void excludePackage() {
        includePackage = false;
    }

    public UserExplorerLastData getUserExplorerLastData() {
        UserExplorerLastData userExplorerLastData = new UserExplorerLastData();

        if (activeOptionsArgumentCaptor != null) {
            userExplorerLastData.setOptions(activeOptionsArgumentCaptor.getValue());
        }

        if (packageArgumentCaptor == null) {
            return userExplorerLastData;
        }

        Package value = packageArgumentCaptor.getValue();
        FolderListing folderListing = folderListingArgumentCaptor.getValue();
        if (folderListing != null) {
            userExplorerLastData.setFolderItem(
                    projectArgumentCaptor.getValue().getRepository(),
                    projectArgumentCaptor.getValue().getBranch().getName(),
                    moduleArgumentCaptor.getValue(),
                    folderListing.getItem());
        }
        if (value != null && includePackage) {
            userExplorerLastData.setPackage(
                    projectArgumentCaptor.getValue().getRepository(),
                    projectArgumentCaptor.getValue().getBranch().getName(),
                    moduleArgumentCaptor.getValue(),
                    value);
        } else if (includePackage) {
            userExplorerLastData.setPackage(
                    projectArgumentCaptor.getValue().getRepository(),
                    projectArgumentCaptor.getValue().getBranch().getName(),
                    moduleArgumentCaptor.getValue(),
                    new Package());
        }

        return userExplorerLastData;
    }

    public void reset() {
        projectArgumentCaptor = ArgumentCaptor.forClass(WorkspaceProject.class);
        moduleArgumentCaptor = ArgumentCaptor.forClass(Module.class);
        folderListingArgumentCaptor = ArgumentCaptor.forClass(FolderListing.class);
        packageArgumentCaptor = ArgumentCaptor.forClass(Package.class);
        activeOptionsArgumentCaptor = ArgumentCaptor.forClass(ActiveOptions.class);

        verify(
                helper,
                atLeastOnce()
        ).store(projectArgumentCaptor.capture(),
                moduleArgumentCaptor.capture(),
                folderListingArgumentCaptor.capture(),
                packageArgumentCaptor.capture(),
                activeOptionsArgumentCaptor.capture());
    }
}
