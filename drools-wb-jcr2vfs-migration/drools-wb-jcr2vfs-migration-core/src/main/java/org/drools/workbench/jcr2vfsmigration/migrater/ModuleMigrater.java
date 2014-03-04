package org.drools.workbench.jcr2vfsmigration.migrater;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.workbench.jcr2vfsmigration.migrater.util.MigrationPathManager;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.repositories.impl.git.GitRepository;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class ModuleMigrater {

    protected static final Logger logger = LoggerFactory.getLogger( ModuleMigrater.class );

    @Inject
    protected RepositoryModuleService jcrRepositoryModuleService;

    @Inject
    protected MigrationPathManager migrationPathManager;

    @Inject
    protected ProjectService projectService;
    
    public void migrateAll() {
        System.out.println( "  Module migration started" );
        Module[] jcrModules = jcrRepositoryModuleService.listModules();
        for ( Module jcrModule : jcrModules ) {
            migrate( jcrModule );
            System.out.format( "    Module [%s] migrated. %n", jcrModule.getName() );
        }
        
        Module globalModule = jcrRepositoryModuleService.loadGlobalModule();
        migrate( globalModule );
        System.out.println( "    Global migrated.");        
        
        System.out.println( "  Module migration ended" );
    }

    private void migrate( Module jcrModule ) {
        //Set up project structure:
        jcrModule.setName(migrationPathManager.normalizePackageName(jcrModule.getName()));

        String[] nameSplit = jcrModule.getName().split("\\.");

        StringBuilder groupIdBuilder = new StringBuilder();
        groupIdBuilder.append(nameSplit[0]);
        for (int i = 1; i < nameSplit.length - 1; i++) {
            groupIdBuilder.append(".");
            groupIdBuilder.append(nameSplit[i]);
        }

        String groupId = groupIdBuilder.toString();
        String artifactId = nameSplit[nameSplit.length - 1];
        GAV gav = new GAV(groupId,
                          artifactId,
                          "0.0.1");
        POM pom = new POM(gav);

        Path modulePath = migrationPathManager.generateRootPath();
        projectService.newProject( makeRepository( modulePath ),
                                   jcrModule.getName(),
                                   pom,
                                   "http://localhost" );
    }

    private org.uberfire.backend.repositories.Repository makeRepository(final Path repositoryRoot) {
        return new GitRepository(){

            @Override
            public Path getRoot() {
                return repositoryRoot;
            }
        };
    }

}
