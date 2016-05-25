/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.FileVisitor;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardCopyOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;

import static org.guvnor.structure.repositories.EnvironmentParameters.*;
import static org.guvnor.structure.server.config.ConfigType.*;

@Service
@ApplicationScoped
public class ExamplesServiceImpl implements ExamplesService {

    private static final Logger logger = LoggerFactory.getLogger( ExamplesServiceImpl.class );

    private static final String PROJECT_DESCRIPTON = "project.description";

    private IOService ioService;
    private ConfigurationFactory configurationFactory;
    private RepositoryFactory repositoryFactory;
    private KieProjectService projectService;
    private RepositoryService repositoryService;
    private OrganizationalUnitService ouService;
    private Event<NewProjectEvent> newProjectEvent;
    private SafeSessionInfo sessionInfo;

    private final Set<Repository> clonedRepositories = new HashSet<Repository>();
    private final Set<ExampleRepository> exampleRepositories = new HashSet<ExampleRepository>();

    public ExamplesServiceImpl() {
        //Zero-parameter Constructor for CDI proxies
    }

    @Inject
    public ExamplesServiceImpl( final @Named("ioStrategy") IOService ioService,
                                final ConfigurationFactory configurationFactory,
                                final RepositoryFactory repositoryFactory,
                                final KieProjectService projectService,
                                final RepositoryService repositoryService,
                                final OrganizationalUnitService ouService,
                                final Event<NewProjectEvent> newProjectEvent,
                                final SessionInfo sessionInfo ) {
        this.ioService = ioService;
        this.configurationFactory = configurationFactory;
        this.repositoryFactory = repositoryFactory;
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.ouService = ouService;
        this.newProjectEvent = newProjectEvent;
        this.sessionInfo = new SafeSessionInfo( sessionInfo );
    }

    @PostConstruct
    public void loadExampleRepositoryDetails() {
        try {
            final Properties repositoryProperties = new Properties();
            repositoryProperties.load( getClass().getResourceAsStream( "/example-repositories.properties" ) );
            for ( String key : repositoryProperties.stringPropertyNames() ) {
                exampleRepositories.add( new ExampleRepository( repositoryProperties.getProperty( key ) ) );
            }

        } catch ( java.io.IOException e ) {
            logger.error( "Unable to load details of Example Repositories. None will be available in the Workbench.",
                          e );
        }
    }

    @Override
    public ExamplesMetaData getMetaData() {
        return new ExamplesMetaData( getExampleRepositories(),
                                     getExampleOrganizationalUnits() );
    }

    Set<ExampleRepository> getExampleRepositories() {
        return exampleRepositories;
    }

    Set<ExampleOrganizationalUnit> getExampleOrganizationalUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = ouService.getOrganizationalUnits();
        final Set<ExampleOrganizationalUnit> exampleOrganizationalUnits = new HashSet<ExampleOrganizationalUnit>();
        for ( OrganizationalUnit ou : organizationalUnits ) {
            exampleOrganizationalUnits.add( new ExampleOrganizationalUnit( ou.getName() ) );
        }
        return exampleOrganizationalUnits;
    }

    @Override
    public Set<ExampleProject> getProjects( final ExampleRepository repository ) {
        if ( repository == null ) {
            return Collections.emptySet();
        }
        final String repositoryURL = repository.getUrl();
        if ( repositoryURL == null || repositoryURL.trim().isEmpty() ) {
            return Collections.emptySet();
        }
        final Repository gitRepository = cloneRepository( repositoryURL );
        if ( gitRepository == null ) {
            return Collections.emptySet();
        }

        final Set<Project> projects = projectService.getProjects( gitRepository,
                                                                  "master" );
        return convert( projects );
    }

    private Repository cloneRepository( final String repositoryURL ) {
        Repository repository = null;
        try {
            final String alias = getExampleAlias( repositoryURL );
            final Map<String, Object> env = new HashMap<String, Object>() {{
                put( "origin",
                     repositoryURL );
                put( SCHEME,
                     "git" );
            }};

            final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup( REPOSITORY,
                                                                                      alias,
                                                                                      "" );
            for ( final Map.Entry<String, Object> entry : env.entrySet() ) {
                repositoryConfig.addConfigItem( configurationFactory.newConfigItem( entry.getKey(),
                                                                                    entry.getValue() ) );
            }

            repository = repositoryFactory.newRepository( repositoryConfig );
            clonedRepositories.add( repository );
            return repository;

        } catch ( final Exception e ) {
            logger.error( "Error during create repository",
                          e );
            throw new RuntimeException( e );
        }
    }

    private String getExampleAlias( final String repositoryURL ) {
        String alias = repositoryURL;
        alias = alias.substring( alias.lastIndexOf( '/' ) + 1 );
        final int lastDotIndex = alias.lastIndexOf( '.' );
        if ( lastDotIndex > 0 ) {
            alias = alias.substring( 0,
                                     lastDotIndex );
        }
        return "examples-" + alias;
    }

    private Set<ExampleProject> convert( final Set<Project> projects ) {
        final Set<ExampleProject> exampleProjects = new HashSet<ExampleProject>();
        for ( Project project : projects ) {
            exampleProjects.add( new ExampleProject( project.getRootPath(),
                                                     project.getProjectName(),
                                                     readDescription( project ) ) );
        }
        return exampleProjects;
    }

    private String readDescription( final Project project ) {
        final Path root = project.getRootPath();
        final org.uberfire.java.nio.file.Path nioRoot = Paths.convert( root );
        final org.uberfire.java.nio.file.Path nioDescription = nioRoot.resolve( PROJECT_DESCRIPTON );
        String description = "Example '" + project.getProjectName() + "' project";
        if ( ioService.exists( nioDescription ) ) {
            description = ioService.readAllString( nioDescription );
        }
        return description;
    }

    @Override
    public boolean validateRepositoryName( final String name ) {
        return repositoryService.validateRepositoryName( name );
    }

    @Override
    public ProjectContextChangeEvent setupExamples( final ExampleOrganizationalUnit exampleTargetOU,
                                                    final ExampleTargetRepository exampleTarget,
                                                    final List<ExampleProject> exampleProjects ) {
        PortablePreconditions.checkNotNull( "exampleTargetOU",
                                            exampleTargetOU );
        PortablePreconditions.checkNotNull( "exampleTarget",
                                            exampleTarget );
        PortablePreconditions.checkNotNull( "exampleProjects",
                                            exampleProjects );
        PortablePreconditions.checkCondition( "Must have at least one ExampleProject",
                                              exampleProjects.size() > 0 );

        //Retrieve or create Organizational Unit
        final String targetOUName = exampleTargetOU.getName();
        OrganizationalUnit targetOU = ouService.getOrganizationalUnit( targetOUName );
        if ( targetOU == null ) {
            targetOU = createOrganizationalUnit( targetOUName );
        }

        //Retrieve or create target Repository
        final String targetRepositoryAlias = exampleTarget.getAlias();
        Repository targetRepository = repositoryService.getRepository( targetRepositoryAlias );
        if ( targetRepository == null ) {
            targetRepository = createTargetRepository( targetOU,
                                                       targetRepositoryAlias );
        }

        final Path targetRepositoryRoot = targetRepository.getRoot();
        final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot = Paths.convert( targetRepositoryRoot );
        KieProject firstExampleProject = null;

        try {
            ioService.startBatch( nioTargetRepositoryRoot.getFileSystem() );
            for ( ExampleProject exampleProject : exampleProjects ) {
                final Path exampleProjectRoot = exampleProject.getRoot();
                final org.uberfire.java.nio.file.Path nioExampleProjectRoot = Paths.convert( exampleProjectRoot );
                final org.uberfire.java.nio.file.Path nioTargetProjectRoot = nioTargetRepositoryRoot.resolve( exampleProject.getName() );

                final RecursiveCopier copier = new RecursiveCopier( nioExampleProjectRoot,
                                                                    nioTargetProjectRoot );
                Files.walkFileTree( nioExampleProjectRoot,
                                    copier );

                // Signal creation of new Project (Creation of OU and Repository, if applicable,
                // are already handled in the corresponding services).
                final Path targetProjectRoot = Paths.convert( nioTargetProjectRoot );
                final KieProject project = projectService.resolveProject( targetProjectRoot );
                newProjectEvent.fire( new NewProjectEvent( project,
                                                           sessionInfo.getId(),
                                                           sessionInfo.getIdentity().getIdentifier() ) );

                //Store first new example project
                if ( firstExampleProject == null ) {
                    firstExampleProject = project;
                }

            }
        } catch ( IOException ioe ) {
            logger.error( "Unable to create Example(s).",
                          ioe );

        } finally {
            ioService.endBatch();
        }
        return new ProjectContextChangeEvent( targetOU,
                                              targetRepository,
                                              targetRepository.getDefaultBranch(),
                                              firstExampleProject );
    }

    private OrganizationalUnit createOrganizationalUnit( final String name ) {
        final OrganizationalUnit ou = ouService.createOrganizationalUnit( name,
                                                                          "",
                                                                          "" );
        return ou;
    }

    private Repository createTargetRepository( final OrganizationalUnit ou,
                                               final String alias ) {
        final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();
        configuration.setManaged( false );
        final Repository repository = repositoryService.createRepository( ou,
                                                                          GitRepository.SCHEME,
                                                                          alias,
                                                                          configuration );
        return repository;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void dispose() {
        for ( Repository repository : clonedRepositories ) {
            try {
                ioService.delete( Paths.convert( repository.getRoot() ).getFileSystem().getPath( null ) );
            } catch ( Exception e ) {
                logger.warn( "Unable to remove transient Repository '" + repository.getAlias() + "'.",
                             e );
            }
        }
    }

    static class RecursiveCopier implements FileVisitor<org.uberfire.java.nio.file.Path> {

        private final org.uberfire.java.nio.file.Path source;
        private final org.uberfire.java.nio.file.Path target;

        RecursiveCopier( final org.uberfire.java.nio.file.Path source,
                         final org.uberfire.java.nio.file.Path target ) {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult preVisitDirectory( final org.uberfire.java.nio.file.Path src,
                                                  final BasicFileAttributes attrs ) {
            final org.uberfire.java.nio.file.Path tgt = target.resolve( source.relativize( src ) );
            try {
                Files.copy( src,
                            tgt,
                            StandardCopyOption.REPLACE_EXISTING );
            } catch ( FileAlreadyExistsException x ) {
                //Swallow
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile( final org.uberfire.java.nio.file.Path file,
                                          final BasicFileAttributes attrs ) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory( final org.uberfire.java.nio.file.Path dir,
                                                   final IOException exc ) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed( final org.uberfire.java.nio.file.Path file,
                                                final IOException exc ) {
            return FileVisitResult.CONTINUE;
        }
    }

}
