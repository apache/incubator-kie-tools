package org.drools.workbench.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class ModuleMigrater {

    protected static final Logger logger = LoggerFactory.getLogger(ModuleMigrater.class);

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected ProjectService projectService;

    public void migrateAll() {
        logger.info("  Module migration started");
        Module[] jcrModules = jcrRepositoryModuleService.listModules();
        for (Module jcrModule : jcrModules) {
            migrate(jcrModule);
            logger.debug("    Module ({}) migrated.", jcrModule.getName());
        }
        logger.info("  Module migration ended");
    }

    private void migrate(Module jcrModule) {
        //Set up project structure:
        POM pom = new POM();
        Path modulePath = migrationPathManager.generateRootPath();  
        projectService.newProject(modulePath, jcrModule.getName(), pom, "http://localhost");
    }

}
