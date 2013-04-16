package org.uberfire.backend.server.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.commons.services.cdi.Startup;
import org.kie.commons.services.cdi.StartupType;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup {

    private static final String PLAYGROUND_SCHEME = "git";
    private static final String PLAYGROUND_ALIAS = "uf-playground";
    private static final String PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";
    private static final String PLAYGROUND_UID = "guvnorngtestuser1";
    private static final String PLAYGROUND_PWD = "test1234";

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ActiveFileSystemsFactory activeFileSystemsFactory;

    @PostConstruct
    public void assertPlayground() {
        final Repository repository = repositoryService.getRepository( PLAYGROUND_ALIAS );
        if ( repository == null ) {
            repositoryService.cloneRepository( PLAYGROUND_SCHEME,
                                               PLAYGROUND_ALIAS,
                                               PLAYGROUND_ORIGIN,
                                               PLAYGROUND_UID,
                                               PLAYGROUND_PWD );
        }

        //Ensure FileSystems are loaded
        activeFileSystemsFactory.fileSystems();
    }

}
