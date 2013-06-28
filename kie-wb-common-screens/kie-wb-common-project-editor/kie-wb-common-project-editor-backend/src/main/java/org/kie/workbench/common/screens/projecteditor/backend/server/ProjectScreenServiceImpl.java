package org.kie.workbench.common.screens.projecteditor.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.KModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.uberfire.backend.vfs.Path;

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

    @Override
    public ProjectScreenModel load( final Path pathToPom ) {
        ProjectScreenModel model = new ProjectScreenModel();

        model.setPOM( pomService.load( pathToPom ) );
        model.setPOMMetaData( metadataService.getMetadata( pathToPom ) );

        final Project project = projectService.resolveProject( pathToPom );

        Path pathToKModule = project.getKModuleXMLPath();
        model.setKModule( kModuleService.load( pathToKModule ) );
        model.setKModuleMetaData( metadataService.getMetadata( pathToKModule ) );

        Path pathToProjectImports = project.getImportsPath();
        model.setProjectImports( projectService.load( pathToProjectImports ) );
        model.setProjectImportsMetaData( metadataService.getMetadata( pathToProjectImports ) );

        return model;
    }

    @Override
    public void save( final Path pathToPomXML,
                      final ProjectScreenModel model,
                      final String comment ) {
        final Project project = projectService.resolveProject( pathToPomXML );

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
    }
}
