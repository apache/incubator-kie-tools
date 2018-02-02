/*
* Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.rest.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.security.RolesAllowed;
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

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.rest.client.AddProjectToSpaceRequest;
import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectRequest;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveProjectFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.guvnor.rest.client.UpdateOrganizationalUnit;
import org.guvnor.rest.client.UpdateOrganizationalUnitRequest;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.spaces.SpacesAPI;

import static org.guvnor.rest.backend.PermissionConstants.REST_PROJECT_ROLE;
import static org.guvnor.rest.backend.PermissionConstants.REST_ROLE;

/**
 * REST services
 */
@Path("/")
@Named
@ApplicationScoped
public class ProjectResource {

    private static final Logger logger = LoggerFactory.getLogger(ProjectResource.class);
    private static Variant defaultVariant = Variant.mediaTypes(MediaType.APPLICATION_JSON_TYPE).add().build().get(0);
    @Context
    protected UriInfo uriInfo;
    @Inject
    @Named("ioStrategy")
    private IOService ioService;
    @Inject
    private OrganizationalUnitService organizationalUnitService;
    @Inject
    private WorkspaceProjectService workspaceProjectService;
    @Inject
    private SpacesAPI spaces;
    @Inject
    private JobRequestScheduler jobRequestObserver;
    @Inject
    private JobResultManager jobManager;

    @Inject
    private SpacesAPI spacesAPI;

    private AtomicLong counter = new AtomicLong(0);

    private void addAcceptedJobResult(String jobId) {
        JobResult jobResult = new JobResult();
        jobResult.setJobId(jobId);
        jobResult.setStatus(JobStatus.ACCEPTED);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/{jobId}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public JobResult getJobStatus(@PathParam("jobId") String jobId) {
        logger.debug("-----getJobStatus--- , jobId: {}",
                     jobId);

        JobResult job = jobManager.getJob(jobId);
        if (job == null) {
            //the job has gone probably because its done and has been removed.
            logger.debug("-----getJobStatus--- , can not find jobId: " + jobId + ", the job has gone probably because its done and has been removed.");
            job = new JobResult();
            job.setStatus(JobStatus.GONE);
            return job;
        }

        return job;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/{jobId}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public JobResult removeJob(@PathParam("jobId") String jobId) {
        logger.debug("-----removeJob--- , jobId: {}",
                     jobId);

        JobResult job = jobManager.removeJob(jobId);

        if (job == null) {
            //the job has gone probably because its done and has been removed.
            logger.debug("-----removeJob--- , can not find jobId: " + jobId + ", the job has gone probably because its done and has been removed.");
            job = new JobResult();
            job.setStatus(JobStatus.GONE);
            return job;
        }

        job.setStatus(JobStatus.GONE);
        return job;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/git/clone")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response cloneProject(@PathParam("spaceName") String spaceName,
                                 CloneProjectRequest cloneProjectRequest) {
        logger.debug("-----cloneProject--- , CloneProjectRequest name: {}",
                     cloneProjectRequest.getName());

        final String id = newId();
        final CloneProjectJobRequest jobRequest = new CloneProjectJobRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setCloneProjectRequest(cloneProjectRequest);
        addAcceptedJobResult(id);

        jobRequestObserver.cloneProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response createProject(
            @PathParam("spaceName") String spaceName,
            ProjectRequest project) {
        logger.debug("-----createProject--- , spaceName: {} , project name: {}",
                     spaceName,
                     project.getName());

        String id = newId();
        CreateProjectRequest jobRequest = new CreateProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setOrganizationalUnitName(spaceName);
        jobRequest.setProjectName(project.getName());
        jobRequest.setProjectGroupId(project.getGroupId());
        jobRequest.setProjectVersion(project.getVersion());
        jobRequest.setDescription(project.getDescription());
        addAcceptedJobResult(id);

        jobRequestObserver.createProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Collection<ProjectResponse> getProjects(@PathParam("spaceName") String spaceName) {
        logger.info("-----getProjects--- , spaceName: {}",
                    spaceName);
        org.guvnor.structure.organizationalunit.OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit(spaceName);

        if (organizationalUnit == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(organizationalUnit).build());
        }

        Collection<WorkspaceProject> projects = workspaceProjectService.getAllWorkspaceProjects(organizationalUnit);

        List<ProjectResponse> projectRequests = new ArrayList<ProjectResponse>(projects.size());
        for (WorkspaceProject project : projects) {
            ProjectResponse projectReq = new ProjectResponse();
            GAV projectGAV = project.getMainModule().getPom().getGav();
            projectReq.setGroupId(projectGAV.getGroupId());
            projectReq.setVersion(projectGAV.getVersion());
            projectReq.setName(project.getName());
            projectReq.setDescription(project.getMainModule().getPom().getDescription());
            projectRequests.add(projectReq);
        }

        return projectRequests;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/projects")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Collection<ProjectResponse> getProjects() {
        logger.info("-----getProjects--- ");

        return Collections.emptyList();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response deleteProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {
        logger.debug("-----deleteProject--- , project name: {}",
                     projectName);

        String id = newId();
        DeleteProjectRequest jobRequest = new DeleteProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        addAcceptedJobResult(id);

        return createAcceptedStatusResponse(jobRequest);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public ProjectResponse getProject(@PathParam("spaceName") String spaceName, @PathParam("projectName") String projectName) {
        logger.debug("-----getProject---, project name: {}",
                     projectName);

        final ProjectResponse projectResponse = new ProjectResponse();

        return projectResponse;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/compile")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response compileProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {
        logger.debug("-----compileProject--- , project name: {}",
                     projectName);

        String id = newId();
        CompileProjectRequest jobRequest = new CompileProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        addAcceptedJobResult(id);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/install")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response installProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {
        logger.debug("-----installProject--- , project name: {}",
                     projectName);

        PortablePreconditions.checkNotNull("spaceName", spaceName);
        PortablePreconditions.checkNotNull("projectName", projectName);

        String id = newId();
        InstallProjectRequest jobRequest = new InstallProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setProjectName(projectName);
        addAcceptedJobResult(id);

        jobRequestObserver.installProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/test")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response testProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {
        logger.debug("-----testProject--- , project name: {}",
                     projectName);

        String id = newId();
        TestProjectRequest jobRequest = new TestProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        addAcceptedJobResult(id);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/deploy")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response deployProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {
        logger.debug("-----deployProject--- , project name: {}",
                     projectName);

        String id = newId();
        DeployProjectRequest jobRequest = new DeployProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        addAcceptedJobResult(id);

        jobRequestObserver.deployProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Collection<Space> getSpaces() {
        logger.debug("-----getSpaces--- ");

        final List<Space> spaces = new ArrayList<Space>();
        for (OrganizationalUnit ou : organizationalUnitService.getOrganizationalUnits()) {
            final Space space = new Space();
            space.setName(ou.getName());
            space.setOwner(ou.getOwner());
            space.setDefaultGroupId(ou.getDefaultGroupId());
            final List<String> repoNames = new ArrayList<String>();
            for (final Repository r : ou.getRepositories()) {
                final Optional<Branch> defaultBranch = r.getDefaultBranch();
                if (defaultBranch.isPresent()) {
                    final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(defaultBranch.get().getPath());
                    repoNames.add(workspaceProject.getName());
                }
            }
            space.setProjects(repoNames);
            spaces.add(space);
        }

        return spaces;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Space getOrganizationalUnit(@PathParam("spaceName") String spaceName) {
        logger.debug("-----getOrganizationalUnit ---, OrganizationalUnit name: {}",
                     spaceName);

        return null;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response createSpace(Space space) {
        logger.debug("-----createSpace--- , Space name: {}, Space owner: {}, Default group id : {}",
                     space.getName(),
                     space.getOwner(),
                     space.getDefaultGroupId());

        String id = newId();
        SpaceRequest jobRequest = new SpaceRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(space.getName());
        jobRequest.setOwner(space.getOwner());
        jobRequest.setDefaultGroupId(space.getDefaultGroupId());
        jobRequest.setProjects(space.getProjects());
        addAcceptedJobResult(id);

        jobRequestObserver.createOrganizationalUnitRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response updateOrganizationalUnit(@PathParam("spaceName") String orgUnitName,
                                             UpdateOrganizationalUnit organizationalUnit) {

        // use name in url if post entity name is null
        if (organizationalUnit.getName() == null) {
            organizationalUnit.setName(orgUnitName);
        }

        logger.debug("-----updateOrganizationalUnit--- , OrganizationalUnit name: {}, Space owner: {}, Default group id : {}",
                     organizationalUnit.getName(),
                     organizationalUnit.getOwner(),
                     organizationalUnit.getDefaultGroupId());

        String id = newId();
        UpdateOrganizationalUnitRequest jobRequest = new UpdateOrganizationalUnitRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setOrganizationalUnitName(organizationalUnit.getName());
        jobRequest.setOwner(organizationalUnit.getOwner());
        jobRequest.setDefaultGroupId(organizationalUnit.getDefaultGroupId());
        addAcceptedJobResult(id);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response addProjectToSpace(@PathParam("spaceName") String spaceName,
                                      @PathParam("projectName") String projectName) {
        logger.debug("-----addProjectToSpace--- , Space name: {}, Project name: {}",
                     spaceName,
                     projectName);

        final String id = newId();
        final AddProjectToSpaceRequest jobRequest = new AddProjectToSpaceRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setProjectName(projectName);
        addAcceptedJobResult(id);

        jobRequestObserver.addProjectToSpace(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response removeRepositoryFromSpace(@PathParam("spaceName") String spaceName,
                                              @PathParam("projectName") String projectName) {
        logger.debug("-----removeRepositoryFromSpace--- , Space name: {}, Repository name: {}",
                     spaceName,
                     projectName);

        String id = newId();
        RemoveProjectFromOrganizationalUnitRequest jobRequest = new RemoveProjectFromOrganizationalUnitRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setOrganizationalUnitName(spaceName);
        jobRequest.setProjectName(projectName);
        addAcceptedJobResult(id);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response deleteSpace(@PathParam("spaceName") String spaceName) {
        logger.debug("-----deleteSpace--- , Space name: {}",
                     spaceName);

        final String id = newId();
        final RemoveSpaceRequest jobRequest = new RemoveSpaceRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        addAcceptedJobResult(id);

        jobRequestObserver.removeSpaceRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    private Response createAcceptedStatusResponse(JobRequest jobRequest) {
        return Response.status(Status.ACCEPTED).entity(jobRequest).variant(defaultVariant).build();
    }

    private String newId() {
        return "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
    }
}
