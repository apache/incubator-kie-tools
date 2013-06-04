package org.kie.workbench.common.screens.projecteditor.backend.server;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.project.service.KModuleService;
import org.kie.workbench.common.services.project.service.POMService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
    public ProjectScreenModel load(Path pathToPom) {
        ProjectScreenModel model = new ProjectScreenModel();

        model.setPOM(pomService.load(pathToPom));
        model.setPOMMetaData(metadataService.getMetadata(pathToPom));


        Path pathToKModule = kModuleService.pathToRelatedKModuleFileIfAny(pathToPom);
        model.setKModule(kModuleService.load(pathToKModule));
        model.setKModuleMetaData(metadataService.getMetadata(pathToKModule));

        Path pathToProjectImports = projectService.resolvePathToProjectImports(pathToPom);
        model.setProjectImports(projectService.load(pathToProjectImports));
        model.setProjectImportsMetaData(metadataService.getMetadata(pathToProjectImports));

        return model;
    }

    @Override
    public void save(Path pathToPomXML, ProjectScreenModel model, String comment) {

        pomService.save(pathToPomXML, model.getPOM(), model.getPOMMetaData(), comment);
        kModuleService.save(kModuleService.pathToRelatedKModuleFileIfAny(pathToPomXML), model.getKModule(), model.getKModuleMetaData(), comment);
        projectService.save(projectService.resolvePathToProjectImports(pathToPomXML), model.getProjectImports(), model.getProjectImportsMetaData(), comment);

    }
}
