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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.resteasy.annotations.GZIP;
import org.kie.workbench.common.services.rest.RestOperationException;
import org.kie.workbench.common.services.shared.rest.AddRepositoryToOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.BuildConfig;
import org.kie.workbench.common.services.shared.rest.CompileProjectRequest;
import org.kie.workbench.common.services.shared.rest.CreateOrCloneRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.CreateOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.DeployProjectRequest;
import org.kie.workbench.common.services.shared.rest.Entity;
import org.kie.workbench.common.services.shared.rest.InstallProjectRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;
import org.kie.workbench.common.services.shared.rest.OrganizationalUnit;
import org.kie.workbench.common.services.shared.rest.RemoveOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryFromOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.RepositoryRequest;
import org.kie.workbench.common.services.shared.rest.RepositoryResponse;
import org.kie.workbench.common.services.shared.rest.TestProjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.io.IOService;

/**
 * REST services
 */
@Path("/")
@Named
@ApplicationScoped
public class ProjectResource {

    private static final Logger logger = LoggerFactory.getLogger( ProjectResource.class );

    @Context
    protected UriInfo uriInfo;

    @Inject
    protected ProjectService projectService;

    @Inject
    protected BuildService buildService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private JobRequestScheduler jobRequestObserver;

    @Inject
    private JobResultManager jobManager;

    private AtomicLong counter = new AtomicLong( 0 );
   
    private void addAcceptedJobResult(String jobId) { 
        JobResult jobResult = new JobResult();
        jobResult.setJobId( jobId );
        jobResult.setStatus( JobStatus.ACCEPTED );
        jobManager.putJob( jobResult );
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/{jobId}")
    public JobResult getJobStatus( @PathParam("jobId") String jobId ) {
        logger.debug( "-----getJobStatus--- , jobId: {}", jobId );

        JobResult job = jobManager.getJob( jobId );
        if ( job == null ) {
            //the job has gone probably because its done and has been removed.
            logger.debug( "-----getJobStatus--- , can not find jobId:" + jobId + ", the job has gone probably because its done and has been removed." );
            job = new JobResult();
            job.setStatus( JobStatus.GONE );
            return job;
        }

        return job;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/{jobId}")
    public JobResult removeJob( @PathParam("jobId") String jobId ) {
        logger.debug( "-----removeJob--- , jobId: {}", jobId );

        JobResult job = jobManager.removeJob( jobId );

        if ( job == null ) {
            //the job has gone probably because its done and has been removed.
            logger.debug( "-----removeJob--- , can not find jobId:" + jobId + ", the job has gone probably because its done and has been removed." );
            job = new JobResult();
            job.setStatus( JobStatus.GONE );
            return job;
        }

        job.setStatus( JobStatus.GONE );
        return job;
    }

    //TODO: Stop or cancel a job

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories")
    public Collection<RepositoryResponse> getRepositories() {
        logger.debug( "-----getRepositories--- " );

        Collection<org.uberfire.backend.repositories.Repository> repos = repositoryService.getRepositories();
        List<RepositoryResponse> result = new ArrayList<RepositoryResponse>();
        for ( org.uberfire.backend.repositories.Repository r : repos ) {
            RepositoryResponse repo = new RepositoryResponse();
            repo.setGitURL( r.getUri() );
            repo.setName( r.getAlias() );
            result.add( repo );
        }
        return result;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}")
    public RepositoryResponse getRepository(@PathParam("repositoryName") String repositoryName ) {
        logger.debug( "-----getRepository---, repository name: {}",  repositoryName);
        org.uberfire.backend.repositories.Repository origRepo = checkRepositoryExistence(repositoryName);
        
        RepositoryResponse repo = new RepositoryResponse();
        repo.setGitURL( origRepo.getUri() );
        repo.setName( origRepo.getAlias() );
        
        return repo;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories")
    public Response createOrCloneRepository( RepositoryRequest repository ) {
        logger.debug( "-----createOrCloneRepository--- , repository name: {}", repository.getName() );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateOrCloneRepositoryRequest jobRequest = new CreateOrCloneRepositoryRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setRepository( repository );

        addAcceptedJobResult(id);

        jobRequestObserver.createOrCloneRepositoryRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}")
    public Response removeRepository(@PathParam("repositoryName") String repositoryName ) {
        logger.debug( "-----removeRepository--- , repositoryName: {}", repositoryName );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();

        RemoveRepositoryRequest jobRequest = new RemoveRepositoryRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setRepositoryName( repositoryName );

        addAcceptedJobResult(id);

        jobRequestObserver.removeRepositoryRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}/projects")
    public Response createProject(
            @PathParam("repositoryName") String repositoryName,
            Entity project ) {
        logger.debug( "-----createProject--- , repositoryName: {} , project name: {}", repositoryName, project.getName() );
        checkRepositoryExistence(repositoryName);
        
        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateProjectRequest jobRequest = new CreateProjectRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setRepositoryName( repositoryName );
        jobRequest.setProjectName( project.getName() );
        jobRequest.setDescription( project.getDescription() );

        addAcceptedJobResult(id);

        jobRequestObserver.createProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}/projects/{projectName}")
    public Response deleteProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName ) {
        logger.debug( "-----deleteProject--- , repositoryName: {}, project name: {}", repositoryName, projectName );
        checkRepositoryExistence(repositoryName);

        throw new WebApplicationException( Response.status( Response.Status.NOT_ACCEPTABLE )
                                                   .entity( "UNIMPLEMENTED" ).build() );
        
/*        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateProjectRequest jobRequest = new CreateProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobRequest.setProjectName(projectName);
        
        JobResult jobResult = new JobResult();
        jobResult.setJobId(id);
        jobResult.setStatus(JobRequest.Status.ACCEPTED);
        jobs.put(id, jobResult);
        
        //TODO: Delete project. ProjectService does not have a removeProject method yet.
        //createProjectRequestEvent.fire(jobRequest);
        
        return createAcceptedStatusResponse(jobRequest);
        */
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}/projects/{projectName}/maven/compile")
    public Response compileProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName ) {
        logger.debug( "-----compileProject--- , repositoryName: {}, project name: {}", repositoryName, projectName );
        checkRepositoryExistence(repositoryName);

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CompileProjectRequest jobRequest = new CompileProjectRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setRepositoryName( repositoryName );
        jobRequest.setProjectName( projectName );

        addAcceptedJobResult(id);

        jobRequestObserver.compileProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}/projects/{projectName}/maven/install")
    public Response installProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName ) {
        logger.debug( "-----installProject--- , repositoryName: {}, project name: {}" , repositoryName, projectName );
        checkRepositoryExistence(repositoryName);

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        InstallProjectRequest jobRequest = new InstallProjectRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setRepositoryName( repositoryName );
        jobRequest.setProjectName( projectName );

        addAcceptedJobResult(id);

        jobRequestObserver.installProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}/projects/{projectName}/maven/test")
    public Response testProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName,
            BuildConfig mavenConfig ) {
        logger.debug( "-----testProject--- , repositoryName: {}, project name: {}", repositoryName, projectName );
        checkRepositoryExistence(repositoryName);

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        TestProjectRequest jobRequest = new TestProjectRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setRepositoryName( repositoryName );
        jobRequest.setProjectName( projectName );
        jobRequest.setBuildConfig( mavenConfig );

        addAcceptedJobResult(id);

        jobRequestObserver.testProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/repositories/{repositoryName}/projects/{projectName}/maven/deploy")
    public Response deployProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName ) {
        logger.debug( "-----deployProject--- , repositoryName: {}, project name: {}", repositoryName, projectName );
        checkRepositoryExistence(repositoryName);

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        DeployProjectRequest jobRequest = new DeployProjectRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setRepositoryName( repositoryName );
        jobRequest.setProjectName( projectName );

        addAcceptedJobResult(id);

        jobRequestObserver.deployProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/organizationalunits")
    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        logger.debug( "-----getOrganizationalUnits--- " );
        Collection<org.uberfire.backend.organizationalunit.OrganizationalUnit> origOrgUnits
                = organizationalUnitService.getOrganizationalUnits();

        List<OrganizationalUnit> organizationalUnits = new ArrayList<OrganizationalUnit>();
        for ( org.uberfire.backend.organizationalunit.OrganizationalUnit ou : origOrgUnits ) {
            OrganizationalUnit orgUnit = new OrganizationalUnit();
            orgUnit.setName( ou.getName() );
            orgUnit.setOwner( ou.getOwner() );
            List<String> repoNames = new ArrayList<String>();
            for ( Repository r : ou.getRepositories() ) {
                repoNames.add( r.getAlias() );
            }
            orgUnit.setRepositories( repoNames );
            organizationalUnits.add( orgUnit );
        }

        return organizationalUnits;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/organizationalunits/{organizationalUnitName}")
    public OrganizationalUnit getOrganizationalUnit(@PathParam("organizationalUnitName") String organizationalUnitName) { 
        logger.debug( "-----getOrganizationalUnit ---, OrganizationalUnit name: {}", organizationalUnitName );
        org.uberfire.backend.organizationalunit.OrganizationalUnit origOrgUnit
            = checkOrganizationalUnitExistence(organizationalUnitName);
        
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName( origOrgUnit.getName() );
        orgUnit.setOwner( origOrgUnit.getOwner() );
        List<String> repoNames = new ArrayList<String>();
        for ( Repository r : origOrgUnit.getRepositories() ) {
            repoNames.add( r.getAlias() );
        }
        orgUnit.setRepositories( repoNames );

        return orgUnit;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/organizationalunits")
    public Response createOrganizationalUnit( OrganizationalUnit organizationalUnit ) {
        logger.debug( "-----createOrganizationalUnit--- , OrganizationalUnit name: {}, OrganizationalUnit owner: {}", organizationalUnit.getName(), organizationalUnit.getOwner() );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateOrganizationalUnitRequest jobRequest = new CreateOrganizationalUnitRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setOrganizationalUnitName( organizationalUnit.getName() );
        jobRequest.setOwner( organizationalUnit.getOwner() );
        jobRequest.setRepositories( organizationalUnit.getRepositories() );

        addAcceptedJobResult(id);

        jobRequestObserver.createOrganizationalUnitRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/organizationalunits/{organizationalUnitName}/repositories/{repositoryName}")
    public Response addRepositoryToOrganizationalUnit( @PathParam("organizationalUnitName") String organizationalUnitName,
                                                         @PathParam("repositoryName") String repositoryName ) {
        logger.debug( "-----addRepositoryToOrganizationalUnit--- , OrganizationalUnit name: {}, Repository name: {}", organizationalUnitName, repositoryName );
        checkOrganizationalUnitExistence(organizationalUnitName);
        checkRepositoryExistence(repositoryName);

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        AddRepositoryToOrganizationalUnitRequest jobRequest = new AddRepositoryToOrganizationalUnitRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setOrganizationalUnitName( organizationalUnitName );
        jobRequest.setRepositoryName( repositoryName );

        addAcceptedJobResult(id);

        jobRequestObserver.addRepositoryToOrganizationalUnitRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/organizationalunits/{organizationalUnitName}/repositories/{repositoryName}")
    public Response removeRepositoryFromOrganizationalUnit( @PathParam("organizationalUnitName") String organizationalUnitName,
                                                              @PathParam("repositoryName") String repositoryName ) {
        logger.debug( "-----removeRepositoryFromOrganizationalUnit--- , OrganizationalUnit name: {}, Repository name: {}", organizationalUnitName, repositoryName );
        checkOrganizationalUnitExistence(organizationalUnitName);
        checkRepositoryExistence(repositoryName);
        
        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        RemoveRepositoryFromOrganizationalUnitRequest jobRequest = new RemoveRepositoryFromOrganizationalUnitRequest();
        jobRequest.setStatus( JobStatus.ACCEPTED );
        jobRequest.setJobId( id );
        jobRequest.setOrganizationalUnitName( organizationalUnitName );
        jobRequest.setRepositoryName( repositoryName );

        addAcceptedJobResult(id);

        jobRequestObserver.removeRepositoryFromOrganizationalUnitRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/organizationalunits/{organizationalUnitName}")
    public Response deleteOrganizationalUnit( @PathParam("organizationalUnitName") String organizationalUnitName ) {
        logger.debug( "-----deleteOrganizationalUnit--- , OrganizationalUnit name: {}", organizationalUnitName );
        checkOrganizationalUnitExistence(organizationalUnitName);
        
        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        RemoveOrganizationalUnitRequest jobRequest = new RemoveOrganizationalUnitRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setOrganizationalUnitName(organizationalUnitName);

        addAcceptedJobResult(id);
        
        jobRequestObserver.removeOrganizationalUnitRequest(jobRequest);
        
        return createAcceptedStatusResponse(jobRequest);
    }
    
    private org.uberfire.backend.repositories.Repository checkRepositoryExistence(String repoName) { 
        org.uberfire.backend.repositories.Repository repo = repositoryService.getRepository(repoName);
        if( repo == null ) { 
            throw RestOperationException.notFound("Repository " + repoName + " does not exist.");
        }
        return repo;
    }
    
    private org.uberfire.backend.organizationalunit.OrganizationalUnit checkOrganizationalUnitExistence(String orgUnitName) { 
        org.uberfire.backend.organizationalunit.OrganizationalUnit origOrgUnit
            = organizationalUnitService.getOrganizationalUnit(orgUnitName);
        
        if( origOrgUnit == null ) { 
            throw RestOperationException.notFound("Organizational unit " + orgUnitName + " does not exist.");
        }
        return origOrgUnit;
    }
   
    private static Variant defaultVariant = Variant.mediaTypes(MediaType.APPLICATION_JSON_TYPE).add().build().get(0);
    
    private Response createAcceptedStatusResponse(JobRequest jobRequest) { 
        return Response.status(Status.ACCEPTED).entity(jobRequest).variant(defaultVariant).build();
    }

}
