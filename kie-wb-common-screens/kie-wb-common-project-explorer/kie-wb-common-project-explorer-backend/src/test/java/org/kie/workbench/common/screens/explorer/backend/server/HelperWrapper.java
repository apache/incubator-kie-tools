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

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class HelperWrapper {

    private ArgumentCaptor<OrganizationalUnit> organizationalUnitArgumentCaptor;
    private ArgumentCaptor<Repository> repositoryArgumentCaptor;
    private ArgumentCaptor<String> branchArgumentCaptor;
    private ArgumentCaptor<Project> projectArgumentCaptor;
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
                    organizationalUnitArgumentCaptor.getValue(),
                    repositoryArgumentCaptor.getValue(),
                    branchArgumentCaptor.getValue(),
                    projectArgumentCaptor.getValue(),
                    folderListing.getItem());
        }
        if ( value != null && includePackage ) {
            userExplorerLastData.setPackage(
                    organizationalUnitArgumentCaptor.getValue(),
                    repositoryArgumentCaptor.getValue(),
                    branchArgumentCaptor.getValue(),
                    projectArgumentCaptor.getValue(),
                    value);
        } else if( includePackage ){
            userExplorerLastData.setPackage(
                    organizationalUnitArgumentCaptor.getValue(),
                    repositoryArgumentCaptor.getValue(),
                    branchArgumentCaptor.getValue(),
                    projectArgumentCaptor.getValue(),
                    new Package());

        }

        return userExplorerLastData;
    }

    public void reset() {
        organizationalUnitArgumentCaptor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        repositoryArgumentCaptor = ArgumentCaptor.forClass(Repository.class);
        branchArgumentCaptor = ArgumentCaptor.forClass(String.class);
        projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);
        folderListingArgumentCaptor = ArgumentCaptor.forClass(FolderListing.class);
        packageArgumentCaptor = ArgumentCaptor.forClass(Package.class);
        activeOptionsArgumentCaptor = ArgumentCaptor.forClass(ActiveOptions.class);

        verify(
                helper,
                atLeastOnce()
        ).store(organizationalUnitArgumentCaptor.capture(),
                repositoryArgumentCaptor.capture(),
                branchArgumentCaptor.capture(),
                projectArgumentCaptor.capture(),
                folderListingArgumentCaptor.capture(),
                packageArgumentCaptor.capture(),
                activeOptionsArgumentCaptor.capture());

    }


}
