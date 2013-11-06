package org.kie.workbench.common.services.rest;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;
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
import org.uberfire.io.IOService;
import org.kie.workbench.common.services.shared.rest.BuildConfig;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.RepositoryRequest;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.organizationalunit.impl.OrganizationalUnitImpl;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class ProjectResourceDispatcher {

    @Inject
    RepositoryService repositoryService;

    @Inject
    protected ProjectService projectService;

    @Inject
    @Named("ioStrategy")
    private IOService ioSystemService;

    @Inject
    protected BuildService buildService;

    @Inject
    OrganizationalUnitService organicationalUnitService;

    @Inject
    private Event<JobResult> jobResultEvent;

    @Inject
    protected ScenarioTestEditorService scenarioTestEditorService;

    public void createOrCloneRepository( String jobId,
                                         RepositoryRequest repository ) {
        System.out.println( "-----ProjectResourceDispatcher:createOrCloneRepository--- , repository name:" + repository.getName() );

        JobResult result = new JobResult();
        result.setJodId( jobId );

        if ( repository.getRequestType() == null || "".equals( repository.getRequestType() )
                || !( "new".equals( repository.getRequestType() ) || ( "clone".equals( repository.getRequestType() ) ) ) ) {
            result.setStatus( JobRequest.Status.BAD_REQUEST );
            result.setResult( "Repository request type can only be new or clone." );
            jobResultEvent.fire( result );
            return;
        }

        final String scheme = "git";

        if ( "new".equals( repository.getRequestType() ) ) {
            if ( repository.getName() == null || "".equals( repository.getName() ) ) {
                result.setStatus( JobRequest.Status.BAD_REQUEST );
                result.setResult( "Repository name must be provided" );
                jobResultEvent.fire( result );
                return;
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
                result.setStatus( JobRequest.Status.SUCCESS );
                result.setResult( "Alias: " + newlyCreatedRepo.getAlias() + ", Scheme: " + newlyCreatedRepo.getScheme() + ", Uri: " + newlyCreatedRepo.getUri() );
            } else {
                result.setStatus( JobRequest.Status.FAIL );
            }

        } else if ( "clone".equals( repository.getRequestType() ) ) {
            if ( repository.getName() == null || "".equals( repository.getName() ) || repository.getGitURL() == null
                    || "".equals( repository.getGitURL() ) ) {
                result.setStatus( JobRequest.Status.BAD_REQUEST );
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
                result.setStatus( JobRequest.Status.SUCCESS );
                result.setResult( "Alias: " + newlyCreatedRepo.getAlias() + ", Scheme: " + newlyCreatedRepo.getScheme() + ", Uri: " + newlyCreatedRepo.getUri() );
            } else {
                result.setStatus( JobRequest.Status.FAIL );
            }
        }

        jobResultEvent.fire( result );
    }

    public void removeRepository( String jobId,
                                  String repositoryName ) {
        System.out.println( "-----removeRepository--- , repository name:" + repositoryName );

        JobResult result = new JobResult();
        result.setJodId( jobId );

        if ( repositoryName == null || "".equals( repositoryName ) ) {
            result.setStatus( JobRequest.Status.BAD_REQUEST );
            result.setResult( "Repository name must be provided" );
        }

        repositoryService.removeRepository( repositoryName );

        result.setStatus( JobRequest.Status.SUCCESS );
        jobResultEvent.fire( result );
    }

    public void createProject( String jobId,
                               String repositoryName,
                               String projectName ) {
        System.out.println( "-----ProjectResourceDispatcher:createProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );
        JobResult result = new JobResult();
        result.setJodId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            jobResultEvent.fire( result );
            return;
        } else {
            POM pom = new POM();
            pom.getGav().setArtifactId( projectName );
            pom.getGav().setGroupId( projectName );
            pom.getGav().setVersion( "1.0" );

            try {
                Project project = projectService.newProject( makeRepository( Paths.convert( repositoryPath ) ),
                                                             projectName,
                                                             pom,
                                                             "/" );
            } catch ( org.uberfire.java.nio.file.FileAlreadyExistsException e ) {
                result.setStatus( JobRequest.Status.DUPLICATE_RESOURCE );
                result.setResult( "Project [" + projectName + "] already exists" );
                jobResultEvent.fire( result );
                return;
            }

            //TODO: handle errors, exceptions.

            result.setStatus( JobRequest.Status.SUCCESS );
            jobResultEvent.fire( result );
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

    public void compileProject( String jobId,
                                String repositoryName,
                                String projectName) {
        System.out.println( "-----ProjectResourceDispatcher:compileProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );
        JobResult result = new JobResult();
        result.setJodId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            jobResultEvent.fire( result );
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                jobResultEvent.fire( result );
                return;
            }

            BuildResults buildResults = buildService.build( project );

            result.setDetailedResult( buildResultsToDetailedStringMessages( buildResults.getMessages() ) );
            result.setStatus( buildResults.getMessages().isEmpty() ? JobRequest.Status.SUCCESS : JobRequest.Status.FAIL );
            jobResultEvent.fire( result );
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

    public void installProject( String jobId,
                                String repositoryName,
                                String projectName) {
        System.out.println( "-----ProjectResourceDispatcher:installProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );
        JobResult result = new JobResult();
        result.setJodId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            jobResultEvent.fire( result );
            return;
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                jobResultEvent.fire( result );
                return;
            }

            BuildResults buildResults = null;
            try {
                buildResults = buildService.buildAndDeploy( project );

                result.setDetailedResult( buildResults == null ? null : deployResultToDetailedStringMessages( buildResults ) );
                result.setStatus( buildResults.getMessages().isEmpty() ? JobRequest.Status.SUCCESS : JobRequest.Status.FAIL );
            } catch (Throwable t) {
                List<String> errorResult = new ArrayList<String>();
                errorResult.add( t.getMessage());
                result.setDetailedResult( errorResult );
                result.setStatus( JobRequest.Status.FAIL );           	
            }

            jobResultEvent.fire( result );
        }
    }

    private List<String> deployResultToDetailedStringMessages( BuildResults deployResult ) {
        GAV gav = deployResult.getGAV();
        List<String> result = buildResultsToDetailedStringMessages( deployResult.getMessages() );
        String detailedStringMessage = "artifactID:" + gav.getArtifactId() +
                ", groupId:" + gav.getGroupId() +
                ", version:" + gav.getVersion();
        result.add( detailedStringMessage );
        return result;
    }

    public void testProject( String jobId,
                             String repositoryName,
                             String projectName,
                             BuildConfig config ) {
        System.out.println( "-----ProjectResourceDispatcher:testProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );
        final JobResult result = new JobResult();
        result.setJodId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            jobResultEvent.fire( result );
            return;
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                jobResultEvent.fire( result );
                return;
            }

            //TODO: Get session from BuildConfig or create a default session for testing if no session is provided.
            scenarioTestEditorService.runAllScenarios( project.getPomXMLPath(), new Event<TestResultMessage>() {
                @Override
                public void fire( TestResultMessage event ) {
                    result.setDetailedResult( testResultMessageToDetailedStringMessages( event ) );
                    result.setStatus( event.wasSuccessful() ? JobRequest.Status.SUCCESS : JobRequest.Status.FAIL );
                    jobResultEvent.fire( result );
                }

                //@Override
                public Event<TestResultMessage> select( Annotation... qualifiers ) {
                    // TODO Auto-generated method stub
                    return null;
                }

                //@Override
                public <U extends TestResultMessage> Event<U> select(
                        Class<U> subtype,
                        Annotation... qualifiers ) {
                    // TODO Auto-generated method stub
                    return null;
                }

                //@Override
                public <U extends TestResultMessage> Event<U> select(
                        TypeLiteral<U> subtype,
                        Annotation... qualifiers ) {
                    // TODO Auto-generated method stub
                    return null;
                }
            } );
        }
    }

    private List<String> testResultMessageToDetailedStringMessages( TestResultMessage message ) {
        List<String> result = new ArrayList<String>();
        result.add( "wasSuccessuful: " + message.wasSuccessful() );
        result.add( "RunCoun: " + message.getRunCount() );
        result.add( "FailureCount: " + message.getFailureCount() );
        for ( Failure failure : message.getFailures() ) {
            result.add( "Failure: " + failure.getMessage() );
        }
        return result;
    }

    public void deployProject( String jobId,
                               String repositoryName,
                               String projectName ) {
        System.out.println( "-----ProjectResourceDispatcher:deployProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );
        JobResult result = new JobResult();
        result.setJodId( jobId );

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );

        if ( repositoryPath == null ) {
            result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            jobResultEvent.fire( result );
            return;
        } else {
            Project project = projectService.resolveProject( Paths.convert( repositoryPath.resolve( projectName ) ) );

            if ( project == null ) {
                result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
                result.setResult( "Project [" + projectName + "] does not exist" );
                jobResultEvent.fire( result );
                return;
            }

            BuildResults buildResults = buildService.buildAndDeploy( project );

            result.setDetailedResult( buildResults == null ? null : deployResultToDetailedStringMessages( buildResults ) );
            result.setStatus( buildResults.getMessages().isEmpty() ? JobRequest.Status.SUCCESS : JobRequest.Status.FAIL );
            jobResultEvent.fire( result );
        }
    }

    public void createOrganizationalUnit( String jobId,
                                          String organizationalUnitName,
                                          String organizationalUnitOwner,
                                          List<String> repositoryNameList ) {
        System.out.println( "-----ProjectResourceDispatcher:createOrganizationalUnit--- , OrganizationalUnit name:" + organizationalUnitName + ", OrganizationalUnit owner:" + organizationalUnitOwner );
        JobResult result = new JobResult();
        result.setJodId( jobId );

        if ( organizationalUnitName == null || organizationalUnitName == null ) {
            result.setStatus( JobRequest.Status.BAD_REQUEST );
            result.setResult( "OrganizationalUnit name and owner must be provided" );
            jobResultEvent.fire( result );
            return;
        }

        OrganizationalUnit organizationalUnit = null;
        List<org.uberfire.backend.repositories.Repository> repositories = new ArrayList<org.uberfire.backend.repositories.Repository>();
        if ( repositoryNameList != null && repositoryNameList.size() > 0 ) {
            for ( String repoName : repositoryNameList ) {
                org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repoName );

                if(repositoryPath == null) {
                    result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
                    result.setResult( "Repository [" + repoName + "] does not exist" );
                    jobResultEvent.fire( result );
                    return;
                } 
                GitRepository repo = new GitRepository( repoName );
                repositories.add( repo );
            }
            organizationalUnit = organicationalUnitService.createOrganizationalUnit( organizationalUnitName,
                                                                                     organizationalUnitOwner,
                                                                                     repositories );
        } else {
            organizationalUnit = organicationalUnitService.createOrganizationalUnit( organizationalUnitName,
                                                                                     organizationalUnitOwner );
        }

        if ( organizationalUnit != null ) {
            result.setResult( "OrganizationalUnit " + organizationalUnit.getName() + " is created successfully." );
            result.setStatus( JobRequest.Status.SUCCESS );
        } else {
            result.setStatus( JobRequest.Status.FAIL );
        }
        jobResultEvent.fire( result );
    }

    public void addRepositoryToOrganizationalUnit( String jobId,
                                                   String organizationalUnitName,
                                                   String repositoryName ) {
        System.out.println( "-----ProjectResourceDispatcher:addRepositoryToOrganizationalUnit--- , OrganizationalUnit name:" + organizationalUnitName + ", repository name:" + repositoryName );
        JobResult result = new JobResult();
        result.setJodId( jobId );

        if ( organizationalUnitName == null || repositoryName == null ) {
            result.setStatus( JobRequest.Status.BAD_REQUEST );
            result.setResult( "OrganizationalUnit name and Repository name must be provided" );
            jobResultEvent.fire( result );
            return;
        }
        
        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );
        if(repositoryPath == null) {
            result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            jobResultEvent.fire( result );
            return;
        } 
        
        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl( organizationalUnitName,
                                                                            null );
       
        GitRepository repo = new GitRepository( repositoryName );        
        try {
            organicationalUnitService.addRepository( organizationalUnit,
                                                     repo );
        } catch ( IllegalArgumentException e ) {
            result.setStatus( JobRequest.Status.BAD_REQUEST );
            result.setResult( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
            jobResultEvent.fire( result );
            return;
        }

        result.setStatus( JobRequest.Status.SUCCESS );
        jobResultEvent.fire( result );
    }

    public void removeRepositoryFromOrganizationalUnit( String jobId,
                                                        String organizationalUnitName,
                                                        String repositoryName ) {
        System.out.println( "-----ProjectResourceDispatcher:removeRepositoryFromOrganizationalUnit--- , OrganizationalUnit name:" + organizationalUnitName + ", repository name:" + repositoryName );
        JobResult result = new JobResult();
        result.setJodId( jobId );

        if ( organizationalUnitName == null || repositoryName == null ) {
            result.setStatus( JobRequest.Status.BAD_REQUEST );
            result.setResult( "OrganizationalUnit name and Repository name must be provided" );
            jobResultEvent.fire( result );
            return;
        }

        org.uberfire.java.nio.file.Path repositoryPath = getRepositoryRootPath( repositoryName );
        if(repositoryPath == null) {
            result.setStatus( JobRequest.Status.RESOURCE_NOT_EXIST );
            result.setResult( "Repository [" + repositoryName + "] does not exist" );
            jobResultEvent.fire( result );
            return;
        } 
        
        OrganizationalUnit organizationalUnit = new OrganizationalUnitImpl( organizationalUnitName, null );
        GitRepository repo = new GitRepository( repositoryName );
        try {
            organicationalUnitService.removeRepository( organizationalUnit,
                                                        repo );
        } catch ( IllegalArgumentException e ) {
            result.setStatus( JobRequest.Status.BAD_REQUEST );
            result.setResult( "OrganizationalUnit " + organizationalUnit.getName() + " not found" );
            jobResultEvent.fire( result );
            return;
        }

        result.setStatus( JobRequest.Status.SUCCESS );
        jobResultEvent.fire( result );
    }

    public org.uberfire.java.nio.file.Path getRepositoryRootPath( String repositoryName ) {
        org.uberfire.backend.repositories.Repository repo = repositoryService.getRepository( repositoryName );
        if ( repo == null ) {
            return null;
        }
        return Paths.convert( repo.getRoot() );
    }
}
