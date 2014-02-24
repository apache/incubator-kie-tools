package org.kie.workbench.common.screens.projecteditor.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.KModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class ProjectScreenServiceImpl
        implements ProjectScreenService {

    @Inject
    private POMService pomService;

    @Inject
    private KModuleService kModuleService;

    @Inject
    private ProjectService projectService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private ValidationService validationService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public ProjectScreenModel load( final Path pathToPom ) {
        ProjectScreenModel model = new ProjectScreenModel();

        model.setPOM( pomService.load( pathToPom ) );
        model.setPOMMetaData( metadataService.getMetadata( pathToPom ) );
        model.setPathToPOM( pathToPom );

        final Project project = projectService.resolveProject( pathToPom );

        Path pathToKModule = project.getKModuleXMLPath();
        model.setKModule( kModuleService.load( pathToKModule ) );
        model.setKModuleMetaData( metadataService.getMetadata( pathToKModule ) );
        model.setPathToKModule( pathToKModule );

        Path pathToProjectImports = project.getImportsPath();
        model.setProjectImports( projectService.load( pathToProjectImports ) );
        model.setProjectImportsMetaData( metadataService.getMetadata( pathToProjectImports ) );
        model.setPathToImports( pathToProjectImports );

        return model;
    }

    @Override
    public void save( final Path pathToPomXML,
                      final ProjectScreenModel model,
                      final String comment ) {
        final Project project = projectService.resolveProject( pathToPomXML );

        try {
            ioService.startBatch();
            pomService.save( pathToPomXML,
                             model.getPOM(),
                             model.getPOMMetaData(),
                             comment );
            kModuleService.save( project.getKModuleXMLPath(),
                                 model.getKModule(),
                                 model.getKModuleMetaData(),
                                 comment );
            projectService.save( project.getImportsPath(),
                                 model.getProjectImports(),
                                 model.getProjectImportsMetaData(),
                                 comment );
        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public ProjectScreenModel rename( final Path pathToPomXML,
                                      final String newName,
                                      final String comment ) {
        return load( projectService.rename( pathToPomXML, newName, comment ) );
    }

    @Override
    public void delete( final Path pathToPomXML,
                        final String comment ) {
        projectService.delete( pathToPomXML, comment );
    }

    @Override
    public void copy( Path pathToPomXML,
                      String newName,
                      String comment ) {
        projectService.copy( pathToPomXML, newName, comment );
    }

    @Override
    public boolean validate( final POM pom ) {
        PortablePreconditions.checkNotNull( "pom",
                                            pom );
        final String name = pom.getName();
        final String groupId = pom.getGav().getGroupId();
        final String artifactId = pom.getGav().getArtifactId();
        final String version = pom.getGav().getVersion();

        final String[] groupIdComponents = ( groupId == null ? new String[]{ } : groupId.split( "\\.",
                                                                                                -1 ) );
        final String[] artifactIdComponents = ( artifactId == null ? new String[]{ } : artifactId.split( "\\.",
                                                                                                         -1 ) );

        final boolean validName = !( name == null || name.isEmpty() ) && validationService.isProjectNameValid( name );
        final boolean validGroupId = !( groupIdComponents.length == 0 || validationService.evaluateIdentifiers( groupIdComponents ).containsValue( Boolean.FALSE ) );
        final boolean validArtifactId = !( artifactIdComponents.length == 0 || validationService.evaluateArtifactIdentifiers( artifactIdComponents ).containsValue( Boolean.FALSE ) );
        final boolean validVersion = !( version == null || version.isEmpty() || !version.matches( "^[a-zA-Z0-9\\.\\-_]+$" ) );

        return validName && validGroupId && validArtifactId && validVersion;
    }

}
