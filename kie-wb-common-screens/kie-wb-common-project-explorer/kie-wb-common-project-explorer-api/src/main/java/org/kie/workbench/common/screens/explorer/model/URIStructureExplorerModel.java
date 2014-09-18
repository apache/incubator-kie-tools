package org.kie.workbench.common.screens.explorer.model;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class URIStructureExplorerModel {

    private OrganizationalUnit organizationalUnit;
    private Repository repository;
    private Project project;

    public URIStructureExplorerModel() {
    };

    public URIStructureExplorerModel( OrganizationalUnit ou,
                                      Repository repository,
                                      Project project ) {
        this.organizationalUnit = ou;
        this.repository = repository;
        this.project = project;
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
