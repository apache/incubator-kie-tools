package org.kie.workbench.common.screens.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Container to return resolved Project and Package
 */
@Portable
public class ProjectPackage {

    private Project project;
    private Package pkg;

    public ProjectPackage() {
        //For Errai-marshalling
    }

    public ProjectPackage( final Project project,
                           final Package pkg ) {
        this.project = project;
        this.pkg = pkg;
    }

    public Project getProject() {
        return project;
    }

    public Package getPackage() {
        return pkg;
    }
}
