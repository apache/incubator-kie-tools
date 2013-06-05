
/*
* Copyright 2011 JBoss Inc
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

package org.kie.workbench.common.services.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.resteasy.annotations.GZIP;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.FileSystem;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.project.service.model.POM;

import org.kie.workbench.common.services.shared.rest.BuildConfig;
import org.kie.workbench.common.services.shared.rest.CloneRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.CompileProjectRequest;
import org.kie.workbench.common.services.shared.rest.CreateGroupRequest;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.DeployProjectRequest;
import org.kie.workbench.common.services.shared.rest.Entity;
import org.kie.workbench.common.services.shared.rest.Group;
import org.kie.workbench.common.services.shared.rest.InstallProjectRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.Repository;
import org.kie.workbench.common.services.shared.rest.TestProjectRequest;
import org.kie.workbench.common.services.shared.builder.BuildService;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.util.Paths;

@Path("/")
@Named
@GZIP
@ApplicationScoped
public class ProjectResource {

    private HttpHeaders headers;

    @Context
    protected UriInfo uriInfo;

    @Inject
    protected ProjectService projectService;

    @Inject
    protected BuildService buildService;

    @Inject
    protected ScenarioTestEditorService scenarioTestEditorService;

    @Inject
    private Paths paths;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    GroupService groupService;

    @Inject
    RepositoryService repositoryService;

	private static class Cache extends LinkedHashMap<String, JobRequest> {
		private int maxSize = 1000;

		public Cache(int maxSize) {
			this.maxSize = maxSize;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, JobRequest> stringFutureEntry) {
			return size() > maxSize;
		}

		public void setMaxSize(int maxSize) {
			this.maxSize = maxSize;
		}
	}
    private Cache cache;
	private Map<String, JobRequest> jobs;
    private AtomicLong counter = new AtomicLong(0);
   
    private int maxCacheSize = 10000;
    
    @Inject
    private Event<CloneRepositoryRequest> cloneJobRequestEvent;     
    @Inject
    private Event<CreateProjectRequest> createProjectRequestEvent; 
    @Inject
    private Event<CompileProjectRequest> compileProjectRequestEvent; 
    @Inject
    private Event<InstallProjectRequest> installProjectRequestEvent; 
    @Inject
    private Event<TestProjectRequest> testProjectRequestEvent; 
    @Inject
    private Event<DeployProjectRequest> deployProjectRequestEvent; 
    @Inject
    private Event<CreateGroupRequest> createGroupRequestEvent; 
    
    @PostConstruct
    public void start() {
    	cache = new Cache(maxCacheSize);
    	jobs = Collections.synchronizedMap(cache);
    }
    
    @Context
    public void setHttpHeaders( HttpHeaders theHeaders ) {
        headers = theHeaders;
    }
    
    public void onUpateJobStatus( final @Observes JobResult jobResult ) {
    	JobRequest job = jobs.get(jobResult.getJodId());

        if (job == null) {
            //the job has gone probably because its done and has been removed.
        	return;
        }

        job.setStatus(jobResult.getStatus());
        job.setResult(jobResult.getResult());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/{jobId}")
    public JobRequest getJobStatus( @PathParam("jobId") String jobId ) {
        System.out.println( "-----getJobStatus--- , jobId:" + jobId );
        
    	JobRequest job = jobs.get(jobId);

        if (job == null) {
            //the job has gone probably because its done and has been removed.
        	job = new JobRequest();
        	job.setStatus(JobRequest.Status.GONE);
        	return job;
        }

        return job;
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/{jobId}")
    public JobRequest removeJob( @PathParam("jobId") String jobId ) {
        System.out.println( "-----queryJobStatus--- , jobId:" + jobId );
        
    	JobRequest job = jobs.get(jobId);

        if (job == null) {
            //the job has gone probably because its done and has been removed.
        	job = new JobRequest();
        	job.setStatus(JobRequest.Status.GONE);
        	return job;
        }

        jobs.remove(jobId);
        job.setStatus(JobRequest.Status.GONE);
        return job;
    }
    
    //TODO: Stop or cancel a job
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories")
    public JobRequest createOrCloneRepository( Repository repository ) {
        System.out.println( "-----createOrCloneRepository--- , repository name:" + repository.getName() );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CloneRepositoryRequest jobRequest = new CloneRepositoryRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepository(repository);
        jobs.put(id, jobRequest);
        
        cloneJobRequestEvent.fire(jobRequest);
        
        return jobRequest;   
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}")
    public JobRequest deleteRepository(
            @PathParam("repositoryName") String repositoryName ) {
        System.out.println( "-----deleteRepository--- , repositoryName:" + repositoryName );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        
        //TODO:
        CreateProjectRequest jobRequest = new CreateProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobs.put(id, jobRequest);
        return jobRequest;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects")
    public JobRequest createProject(
            @PathParam("repositoryName") String repositoryName,
            Entity project ) {
        System.out.println( "-----createProject--- , repositoryName:" + repositoryName + ", project name:" + project.getName() );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateProjectRequest jobRequest = new CreateProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobRequest.setProjectName(project.getName());
        jobRequest.setDescription(project.getDescription());
        jobs.put(id, jobRequest);
        
        createProjectRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}")
    public JobRequest deleteProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName ) {
        System.out.println( "-----deleteProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateProjectRequest jobRequest = new CreateProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobRequest.setProjectName(projectName);
        jobs.put(id, jobRequest);
        
        //TODO: Delete project. ProjectService does not have a removeProject method yet.
        //createProjectRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    @GET
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/compile")
    public JobRequest compileProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName,
            BuildConfig mavenConfig ) {
        System.out.println( "-----compileProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CompileProjectRequest jobRequest = new CompileProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobRequest.setBuildConfig(mavenConfig);
        jobs.put(id, jobRequest);
        
        compileProjectRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    @GET
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/install")
    public JobRequest installProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName,
            BuildConfig mavenConfig ) {
        System.out.println( "-----installProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        InstallProjectRequest jobRequest = new InstallProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobRequest.setBuildConfig(mavenConfig);
        jobs.put(id, jobRequest);
        
        installProjectRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    @GET
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/test")
    public JobRequest testProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName,
            BuildConfig mavenConfig ) {
        System.out.println( "-----testProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        TestProjectRequest jobRequest = new TestProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobRequest.setBuildConfig(mavenConfig);
        jobs.put(id, jobRequest);
        
        testProjectRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("repositories/{repositoryName}/projects/{projectName}/maven/deploy")
    public JobRequest deployProject(
            @PathParam("repositoryName") String repositoryName,
            @PathParam("projectName") String projectName,
            BuildConfig mavenConfig ) {
        
        System.out.println( "-----deployProject--- , repositoryName:" + repositoryName + ", project name:" + projectName );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        DeployProjectRequest jobRequest = new DeployProjectRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setRepositoryName(repositoryName);
        jobRequest.setBuildConfig(mavenConfig);
        jobs.put(id, jobRequest);
        
        deployProjectRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups")
    public JobRequest createGroup( Group group ) {
        System.out.println( "-----createGroup--- , Group name:" + group.getName() + ", Group owner:" + group.getOwner() );

        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateGroupRequest jobRequest = new CreateGroupRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setGroupName(group.getName());
        jobRequest.setOwnder(group.getOwner());
        jobs.put(id, jobRequest);
        
        createGroupRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/groups/{groupName}")
    public JobRequest deleteGroup( @PathParam("groupName") String groupName ) {
        System.out.println( "-----deleteGroup--- , Group name:" + groupName );

        //TODO:GroupService does not have removeGroup method yet
        //groupService.removeGroup(groupName);
        
        String id = "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
        CreateGroupRequest jobRequest = new CreateGroupRequest();
        jobRequest.setStatus(JobRequest.Status.ACCEPTED);
        jobRequest.setJodId(id);
        jobRequest.setGroupName(groupName);
        jobs.put(id, jobRequest);
        
        //TODO:GroupService does not have removeGroup method yet
        //groupService.removeGroup(groupName);
        //createGroupRequestEvent.fire(jobRequest);
        
        return jobRequest;
    }

    public org.kie.commons.java.nio.file.Path getRepositoryRootPath( String repositoryName ) {
        org.kie.commons.java.nio.file.Path repositoryRootPath = null;

        final Iterator<FileSystem> fsIterator = ioService.getFileSystems().iterator();

        if ( fsIterator.hasNext() ) {
            final FileSystem fileSystem = fsIterator.next();
            System.out.println( "-----FileSystem id--- :" + ( (org.kie.commons.java.nio.base.FileSystemId) fileSystem ).id() );

            if ( repositoryName.equalsIgnoreCase( ( (org.kie.commons.java.nio.base.FileSystemId) fileSystem ).id() ) ) {
                final Iterator<org.kie.commons.java.nio.file.Path> rootIterator = fileSystem.getRootDirectories().iterator();
                if ( rootIterator.hasNext() ) {
                    repositoryRootPath = rootIterator.next();
                    System.out.println( "-----rootPath--- :" + repositoryRootPath );

                    org.kie.commons.java.nio.file.DirectoryStream<org.kie.commons.java.nio.file.Path> paths = ioService
                            .newDirectoryStream( repositoryRootPath );
                    for ( final org.kie.commons.java.nio.file.Path child : paths ) {
                        System.out.println( "-----child--- :" + child );
                    }

                    return repositoryRootPath;
                }
            }
        }

        return repositoryRootPath;
    }
}





