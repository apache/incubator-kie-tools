/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.backend.server.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup( StartupType.BOOTSTRAP )
@ApplicationScoped
public class AppSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger( AppSetup.class );

    @Inject
    @Named( "ioStrategy" )
    private IOService ioService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private KieProjectService projectService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @PostConstruct
    public void assertPlayground() {
        try {
            configurationService.startBatch();
            Repository exampleRepo = createRepository( "repository1",
                    "git",
                    null,
                    "",
                    "" );
            createOU( exampleRepo,
                    "example",
                    "" );

            createProject( exampleRepo,
                    "org.kie.example",
                    "project1",
                    "1.0.0-SNAPSHOT" );

        } catch ( final Exception e ) {
            LOGGER.error( "Error during update config", e );
            throw new RuntimeException( e );
        } finally {
            configurationService.endBatch();
        }
    }

    private Repository createRepository( final String alias,
                                         final String scheme,
                                         final String origin,
                                         final String user,
                                         final String password ) {
        Repository repository = repositoryService.getRepository( alias );
        if ( repository == null ) {
            repository = repositoryService.createRepository( scheme,
                    alias,
                    new HashMap<String, Object>() {{
                        if ( origin != null ) {
                            put( "origin", origin );
                        }
                        put( "username", user );
                        put( "crypt:password", password );
                    }} );
        }
        return repository;
    }

    private OrganizationalUnit createOU( final Repository repository,
                                         final String ouName,
                                         final String ouOwner ) {
        OrganizationalUnit ou = organizationalUnitService.getOrganizationalUnit( ouName );
        if ( ou == null ) {
            List<Repository> repositories = new ArrayList<Repository>();
            repositories.add( repository );
            organizationalUnitService.createOrganizationalUnit( ouName,
                    ouOwner,
                    null,
                    repositories );
        }
        return ou;
    }

    private void createProject( final Repository repository,
                                final String group,
                                final String artifact,
                                final String version ) {
        final GAV gav = new GAV( group,
                artifact,
                version );
        try {
            if ( repository != null ) {
                final String projectLocation = repository.getUri() + ioService.getFileSystem( URI.create( repository.getUri() ) ).getSeparator() + artifact;
                if ( !ioService.exists( ioService.get( URI.create( projectLocation ) ) ) ) {
                    projectService.newProject( repository,
                            artifact,
                            new POM( gav ),
                            "/" );
                }
            } else {
                LOGGER.error( "Repository was not found (is null), cannot add project" );
            }
        } catch ( Exception e ) {
            LOGGER.error( "Unable to bootstrap project {} in repository {}",
                    gav,
                    repository.getAlias(),
                    e );
        }
    }

}
