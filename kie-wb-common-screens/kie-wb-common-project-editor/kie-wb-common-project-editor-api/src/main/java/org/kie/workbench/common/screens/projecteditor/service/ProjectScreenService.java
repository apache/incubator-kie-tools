package org.kie.workbench.common.screens.projecteditor.service;

import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.Path;

@Remote
public interface ProjectScreenService {

    public ProjectScreenModel load( Path path );

    void save( Path pathToPomXML,
               ProjectScreenModel model,
               String comment );

    ProjectScreenModel rename( final Path pathToPomXML,
                               final String renameModel,
                               final String comment );

    void delete( final Path pomXMLPath,
                 final String comment );

    void copy( final Path pomXMLPath,
               final String newFileName,
               final String commitMessage );

    String sanitizeArtifactId( final String artifactId );

    boolean validate( final POM pom );

}
