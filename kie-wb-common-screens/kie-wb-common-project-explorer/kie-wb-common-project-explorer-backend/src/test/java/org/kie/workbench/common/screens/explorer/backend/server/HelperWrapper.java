package org.kie.workbench.common.screens.explorer.backend.server;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class HelperWrapper {

    private ArgumentCaptor<OrganizationalUnit> organizationalUnitArgumentCaptor;
    private ArgumentCaptor<Repository> repositoryArgumentCaptor;
    private ArgumentCaptor<Project> projectArgumentCaptor;
    private ArgumentCaptor<FolderListing> folderListingArgumentCaptor;
    private ArgumentCaptor<Package> packageArgumentCaptor;
    private ExplorerServiceHelper helper;


    public HelperWrapper(ExplorerServiceHelper helper) {
        this.helper = helper;
    }

    public UserExplorerLastData getUserExplorerLastData() {
        UserExplorerLastData userExplorerLastData = new UserExplorerLastData();

        if (packageArgumentCaptor == null) {
            return userExplorerLastData;
        }

        Package value = packageArgumentCaptor.getValue();
        FolderListing folderListing = folderListingArgumentCaptor.getValue();
        if (folderListing != null) {
            userExplorerLastData.setFolderItem(
                    organizationalUnitArgumentCaptor.getValue(),
                    repositoryArgumentCaptor.getValue(),
                    projectArgumentCaptor.getValue(),
                    folderListing.getItem());
        }
        if (value != null) {
            userExplorerLastData.setPackage(
                    organizationalUnitArgumentCaptor.getValue(),
                    repositoryArgumentCaptor.getValue(),
                    projectArgumentCaptor.getValue(),
                    value);

        }

        return userExplorerLastData;
    }

    public void reset() {
        organizationalUnitArgumentCaptor = ArgumentCaptor.forClass(OrganizationalUnit.class);
        repositoryArgumentCaptor = ArgumentCaptor.forClass(Repository.class);
        projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);
        folderListingArgumentCaptor = ArgumentCaptor.forClass(FolderListing.class);
        packageArgumentCaptor = ArgumentCaptor.forClass(Package.class);

        verify(
                helper,
                atLeastOnce()
        ).store(organizationalUnitArgumentCaptor.capture(),
                repositoryArgumentCaptor.capture(),
                projectArgumentCaptor.capture(),
                folderListingArgumentCaptor.capture(),
                packageArgumentCaptor.capture(),
                anySet());

    }


}
