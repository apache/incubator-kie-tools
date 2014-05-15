/*
* Copyright 2013 JBoss Inc
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
package org.drools.workbench.common.services.rest;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.screens.testscenario.model.Failure;
import org.drools.workbench.screens.testscenario.model.TestResultMessage;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.rest.BuildConfig;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;
import org.kie.workbench.common.services.shared.rest.RepositoryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.organizationalunit.impl.OrganizationalUnitImpl;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

/**
 * Utility class to perform various functions for the REST service involving backend services
 */
@ApplicationScoped
public class JobRequestHelper {

    // TODO: add more logging in this class? (not at the beginning of methods, but during.. )
    private static final Logger logger = LoggerFactory.getLogger( JobRequestHelper.class );

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private ProjectService projectService;

    @Inject
    @Named("ioStrategy")
    private IOService ioSystemService;

    @Inject
    private BuildService buildService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private ScenarioTestEditorService scenarioTestEditorService;

    public JobResult createOrCloneRepository( final String jobId, final RepositoryRequest repository ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        if ( repository.getRequestType() == null || "".equals( repository.getRequestType() )
                || !( "new".equals( repository.getRequestType() ) || ( "clone".equals( repository.getRequestType() ) ) ) ) {
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "Repository request type can only be new or clone." );
            return result;
        }

        final String scheme = "git";

        if ( "new".equals( repository.getRequestType() ) ) {
            if ( repository.getName() == null || "".equals( repository.getName() ) ) {
                result.setStatus( JobStatus.BAD_REQUEST );
                result.setResult( "Repository name must be provided" );
                return result;
            }

            // username and password are optional
            final Map<String, Object> env = new HashMap<String, Object>( 3 );
            if ( repository.getUserName() != null && !"".equals( repository.getUserName() ) ) {
                env.put( "username", repository.getUserName() );
            }
            if ( repository.getPassword() != null && !"".equals( repository.getPassword() ) ) {
                env.put( "crypt:password", repository.getPassword() );
            }
            env.put( "init", true );

            org.uberfire.backend.repositories.Repository newlyCreatedRepo = repositoryService.createRepository( scheme, repository.getName(), env );
            if ( newlyCreatedRepo != null ) {
                result.setStatus( JobStatus.SUCCESS );
                result.setResult( "Alias: " + newlyCreatedRepo.getAlias() + ", Scheme: " + newlyCreatedRepo.getScheme() + ", Uri: " + newlyCreatedRepo.getUri() );
            } else {
                result.setStatus( JobStatus.FAIL );
            }

        } else if ( "clone".equals( repository.getRequestType() ) ) {
            if ( repository.getName() == null || "".equals( repository.getName() ) || repository.getGitURL() == null
                    || "".equals( repository.getGitURL() ) ) {
                result.setStatus( JobStatus.BAD_REQUEST );
                result.setResult( "Repository name and GitURL must be provided" );
            }

            // username and password are optional
            final Map<String, Object> env = new HashMap<String, Object>( 3 );
            if ( repository.getUserName() != null && !"".equals( repository.getUserName() ) ) {
                env.put( "username", repository.getUserName() );
            }
            if ( repository.getPassword() != null && !"".equals( repository.getPassword() ) ) {
                env.put( "crypt:password", repository.getPassword() );
            }
            env.put( "origin", repository.getGitURL() );

            org.uberfire.backend.repositories.Repository newlyCreatedRepo = repositoryService.createRepository( scheme, repository.getName(), env );
            if ( newlyCreatedRepo != null ) {
                result.setStatus( JobStatus.SUCCESS );
                result.setResult( "Alias: " + newlyCreatedRepo.getAlias() + ", Scheme: " + newlyCreatedRepo.getScheme() + ", Uri: " + newlyCreatedRepo.getUri() );
            } else {
                result.setStatus( JobStatus.FAIL );
            }
        }

        return result;
    }

    public JobResult removeRepository( final String jobId, final String repositoryName ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        if ( repositoryName == null || "".equals( repositoryName ) ) {
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "Repository name must be provided" );
            return result;
        }

        repositoryService.removeRepository( repositoryName );

        result.setStatus( JobStatus.SUCCESS );
        return result;
    }

    public JobResult createProject( final String jobId, final String repositoryName, final String projectName ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            return result;
        } else {
            POM pom = new POM();
            pom.getGav().setArtifactId( projectName );
            pom.getGav().setGroupId( projectName );
            pom.getGav().setVersion( "1.0" );

            try {
                projectService.newProject( makeRepository( Paths.convert( repositoryPath ) ),
                                           projectName,
                                           pom,
                                           "/" );
            } catch ( org.uberfire.java.nio.file.FileAlreadyExistsException e ) {
                result.setStatus( JobStatus.DUPLICATE_RESOURCE );
                result.setResult( "Project [" + projectName + "] already exists" );
                return result;
            }

            //TODO: handle errors, exceptions.

            result.setStatus( JobStatus.SUCCESS );
            return result;
        }
    }

    private org.uberfire.backend.repositories.Repository makeRepository( final Path repositoryRoot ) {
        return new GitRepository() {
            @Override
            public Path getRoot() {
                return repositoryRoot;
            }
        };
    }

    public JobResult compileProject( final String jobId, final String repositoryName, final String projectName ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            return result;
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                return result;
            }

            BuildResults buildResults = buildService.build( project );

            result.setDetailedResult( buildResultsToDetailedStringMessages( buildResults.getMessages() ) );
            result.setStatus( buildResults.getErrorMessages().isEmpty() ? JobStatus.SUCCESS : JobStatus.FAIL );
            return result;
        }
    }

    private List<String> buildResultsToDetailedStringMessages( List<BuildMessage> messages ) {
        List<String> result = new ArrayList<String>();
        for ( BuildMessage message : messages ) {
            String detailedStringMessage = "level:" + message.getLevel() +
                    ", path:" + message.getPath() +
                    ", text:" + message.getText();
            result.add( detailedStringMessage );
        }
        return result;
    }

    public JobResult installProject( final String jobId, final String repositoryName, final String projectName ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            return result;
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                return result;
            }

            BuildResults buildResults = null;
            try {
                buildResults = buildService.buildAndDeploy( project );

                result.setDetailedResult( buildResults == null ? null : deployResultToDetailedStringMessages( buildResults ) );
                result.setStatus( buildResults != null && buildResults.getErrorMessages().isEmpty() ? JobStatus.SUCCESS : JobStatus.FAIL );
            } catch ( Throwable t ) {
                List<String> errorResult = new ArrayList<String>();
                errorResult.add( t.getMessage() );
                result.setDetailedResult( errorResult );
                result.setStatus( JobStatus.FAIL );
            }
            return result;
        }
    }

    private List<String> deployResultToDetailedStringMessages( final BuildResults deployResult ) {
        GAV gav = deployResult.getGAV();
        List<String> result = buildResultsToDetailedStringMessages( deployResult.getErrorMessages() );
        String detailedStringMessage = "artifactID:" + gav.getArtifactId() +
                ", groupId:" + gav.getGroupId() +
                ", version:" + gav.getVersion();
        result.add( detailedStringMessage );
        return result;
    }

    public JobResult testProject( final String jobId, final String repositoryName, final String projectName, final BuildConfig config ) {
        final JobResult result = new JobResult();
        result.setJobId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            return result;
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                return result;
            }

            //TODO: Get session from BuildConfig or create a default session for testing if no session is provided.
            scenarioTestEditorService.runAllScenarios( project.getPomXMLPath(), new Event<TestResultMessage>() {
                @Override
                public void fire( TestResultMessage event ) {
                    result.setDetailedResult( testResultMessageToDetailedStringMessages( event ) );
                    result.setStatus( event.wasSuccessful() ? JobStatus.SUCCESS : JobStatus.FAIL );
                }

                @Override
                public Event<TestResultMessage> select( Annotation... qualifiers ) {
                    // not used
                    return null;
                }

                @Override
                public <U extends TestResultMessage> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
                    // not used
                    return null;
                }

            } );
            return result;
        }
    }

    private List<String> testResultMessageToDetailedStringMessages( final TestResultMessage message ) {
        List<String> result = new ArrayList<String>();
        result.add( "wasSuccessuful: " + message.wasSuccessful() );
        result.add( "RunCoun: " + message.getRunCount() );
        result.add( "FailureCount: " + message.getFailureCount() );
        for ( Failure failure : message.getFailures() ) {
            result.add( "Failure: " + failure.getMessage() );
        }
        return result;
    }

    public JobResult deployProject( final String jobId, final String repositoryName, final String projectName ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            return result;
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                return result;
            }

            BuildResults buildResults = buildService.buildAndDeploy( project );

            result.setDetailedResult( buildResults == null ? null : deployResultToDetailedStringMessages( buildResults ) );
            result.setStatus( buildResults != null && buildResults.getErrorMessages().isEmpty() ? JobStatus.SUCCESS : JobStatus.FAIL );
            return result;
        }
    }

    public JobResult removeOrganizationalUnit( final String jobId, final String organizationalUnitName) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        if ( organizationalUnitName == null ) { 
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "OrganizationalUnit name must be provided" );
            return result;
        }

        try { 
            organizationalUnitService.removeOrganizationalUnit(organizationalUnitName);
            result.setStatus( JobStatus.SUCCESS );
        } catch(Exception e) { 
            result.setStatus( JobStatus.FAIL );
            String errMsg = e.getClass().getSimpleName() + " thrown when trying to remove '" + organizationalUnitName + "': " + e.getMessage();
            result.setResult(errMsg);
            logger.error(errMsg, e);
        }

        return result;
    }

    public JobResult createOrganizationalUnit( final String jobId, final String organizationalUnitName,
            final String organizationalUnitOwner, final List<String> repositoryNameList ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        if ( organizationalUnitName == null || organizationalUnitOwner == null ) {
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "OrganizationalUnit name and owner must be provided" );
            return result;
        }

        OrganizationalUnit organizationalUnit = null;
        List<org.uberfire.backend.repositories.Repository> repositories = new ArrayList<org.uberfire.backend.repositories.Repository>();
        if ( repositoryNameList != null && repositoryNameList.size() > 0 ) {
            for ( String repoName : repositoryNameList ) {
                org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repoName );

                if ( repositoryPath == null ) {
                    result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
                    result.setResult( "Repository [" + repoName + "] does not exist" );
                    return result;
                }
                GitRepository repo = new GitRepository( repoName );
                repositories.add( repo );
            }
            organizationalUnit = organizationalUnitService.createOrganizationalUnit( organizationalUnitName,
                                                                                     organizationalUnitOwner,
                                                                                     repositories );
        } else {
            organizationalUnit = organizationalUnitService.createOrganizationalUnit( organizationalUnitName,
                                                                                     organizationalUnitOwner );
        }

        if ( organizationalUnit != null ) {
            result.setResult( "OrganizationalUnit " + organizationalUnit.getName() + " is created successfully." );
            result.setStatus( JobStatus.SUCCESS );
        } else {
            result.setStatus( JobStatus.FAIL );
        }
        return result;
    }

    public JobResult addRepositoryToOrganizationalUnit( final String jobId, final String organizationalUnitName, final String repositoryName ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        if ( organizationalUnitName == null || repositoryName == null ) {
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "OrganizationalUnit name and Repository name must be provided" );
            return result;
        }

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );
        if ( repositoryPath == null ) {
            result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            return result;
        }

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl( organizationalUnitName,
                                                                            null );

        GitRepository repo = new GitRepository( repositoryName );
        try {
            organizationalUnitService.addRepository( organizationalUnit,
                                                     repo );
        } catch ( IllegalArgumentException e ) {
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
            return result;
        }

        result.setStatus( JobStatus.SUCCESS );
        return result;
    }

    public JobResult removeRepositoryFromOrganizationalUnit( final String jobId, final String organizationalUnitName, final String repositoryName ) {
        JobResult result = new JobResult();
        result.setJobId( jobId );

        if ( organizationalUnitName == null || repositoryName == null ) {
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "OrganizationalUnit name and Repository name must be provided" );
            
            return result;
        }

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );
        if ( repositoryPath == null ) {
            result.setStatus( JobStatus.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            return result;
        }

        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl( organizationalUnitName, null );
        GitRepository repo = new GitRepository( repositoryName );
        try {
            organizationalUnitService.removeRepository( organizationalUnit,
                                                        repo );
        } catch ( IllegalArgumentException e ) {
            result.setStatus( JobStatus.BAD_REQUEST );
            result.setResult( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
            return result;
        }

        result.setStatus( JobStatus.SUCCESS );
        return result;
    }

    private org.uberfire.java.nio.file.Path getRepositoryRootPath( final String repositoryName ) {
        org.uberfire.backend.repositories.Repository repo = repositoryService.getRepository( repositoryName );
        if ( repo == null ) {
            return null;
        }
        return Paths.convert( repo.getRoot() );
    }
}
