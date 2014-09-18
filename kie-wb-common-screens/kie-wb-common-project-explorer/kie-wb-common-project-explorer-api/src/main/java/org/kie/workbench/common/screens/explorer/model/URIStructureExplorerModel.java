package org.kie.workbench.common.screens.explorer.model;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class URIStructureExplorerModel {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;
    private Project project;
    private Package aPackage;
    private FolderItem folderItem;

    public URIStructureExplorerModel(){};
    public URIStructureExplorerModel( OrganizationalUnit ou,
                                      Repository repository,
                                      Project project,
                                      Package aPackage,
                                      FolderItem folderItem ) {
        this.organizationalUnit = ou;
        this.repository = repository;
        this.project = project;
        this.aPackage = aPackage;
        this.folderItem = folderItem;
    }

    public Package getaPackage() {
        return aPackage;
    }

    public FolderItem getFolderItem() {
        return folderItem;
    }

    public OrganizationalUnit getOrganizationalUnit() {
        return organizationalUnit;
    }

    public Repository getRepository() {
        return repository;
    }

    public Project getProject() {
        return project;
    }
}
