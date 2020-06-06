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
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.rest.client.AddBranchJobRequest;
import org.guvnor.rest.client.AddBranchRequest;
import org.guvnor.rest.client.BranchResponse;
import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveBranchJobRequest;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.PublicURI;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

import static org.guvnor.rest.backend.PermissionConstants.REST_PROJECT_ROLE;
import static org.guvnor.rest.backend.PermissionConstants.REST_ROLE;

/**
 * REST services for project management related operations
 */
@Path("/")
@Named
@ApplicationScoped
public class ProjectResource {

    private static final Logger logger = LoggerFactory.getLogger(ProjectResource.class);
    private Variant defaultVariant = getDefaultVariant();

    protected Variant getDefaultVariant() {
        return Variant.mediaTypes(MediaType.APPLICATION_JSON_TYPE).add().build().get(0);
    }

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
    private JobRequestScheduler jobRequestObserver;
    @Inject
    private JobResultManager jobManager;
    @Inject
    private SpacesAPI spacesAPI;
    @Inject
    private SessionInfo sessionInfo;

    private AtomicLong counter = new AtomicLong(0);

    private void addAcceptedJobResult(String jobId) {
        JobResult jobResult = new JobResult();
        jobResult.setJobId(jobId);
        jobResult.setStatus(JobStatus.ACCEPTED);
        jobManager.putJob(jobResult);
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

        final JobResult job = getJobResult(jobId);
        job.setStatus(JobStatus.GONE);
        return job;
    }

    private JobResult getJobResult(String jobId) {
        final JobResult job = jobManager.removeJob(jobId);

        if (job == null) {
            //the job has gone probably because its done and has been removed.
            logger.debug("-----removeJob--- , can not find jobId: " + jobId + ", the job has gone probably because its done and has been removed.");
            return new JobResult();
        } else {
            return job;
        }
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
            CreateProjectRequest createProjectRequest) {
        logger.debug("-----createProject--- , spaceName: {} , project name: {}",
                     spaceName,
                     createProjectRequest.getName());

        assertObjectExists(organizationalUnitService.getOrganizationalUnit(spaceName),
                           "space",
                           spaceName);

        final String id = newId();
        final CreateProjectJobRequest jobRequest = new CreateProjectJobRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setProjectName(createProjectRequest.getName());
        jobRequest.setProjectGroupId(createProjectRequest.getGroupId());
        jobRequest.setProjectVersion(createProjectRequest.getVersion());
        jobRequest.setDescription(createProjectRequest.getDescription());
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

        assertObjectExists(organizationalUnit,
                           "space",
                           spaceName);

        final Collection<WorkspaceProject> projects = workspaceProjectService.getAllWorkspaceProjects(organizationalUnit);

        final List<ProjectResponse> projectRequests = new ArrayList<ProjectResponse>(projects.size());
        for (WorkspaceProject project : projects) {
            projectRequests.add(getProjectResponse(project));
        }

        return projectRequests;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response deleteProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {
        logger.debug("-----deleteProject--- , space name: {}, project name: {}",
                     spaceName,
                     projectName);

        final org.uberfire.spaces.Space space = spacesAPI.getSpace(spaceName);
        assertObjectExists(organizationalUnitService.getOrganizationalUnit(spaceName),
                           "space",
                           spaceName);
        assertObjectExists(workspaceProjectService.resolveProject(space, projectName),
                           "project",
                           projectName);
        final String id = newId();
        final DeleteProjectRequest jobRequest = new DeleteProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        addAcceptedJobResult(id);

        jobRequestObserver.deleteProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public ProjectResponse getProject(@PathParam("spaceName") String spaceName,
                                      @PathParam("projectName") String projectName) {
        logger.debug("-----getProject---, space name: {}, project name: {}",
                     spaceName,
                     projectName);

        assertObjectExists(organizationalUnitService.getOrganizationalUnit(spaceName),
                           "space",
                           spaceName);

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(spacesAPI.getSpace(spaceName), projectName);

        assertObjectExists(workspaceProject,
                           "project",
                           projectName);

        final ProjectResponse projectResponse = getProjectResponse(workspaceProject);

        return projectResponse;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/branches")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Collection<BranchResponse> getBranches(@PathParam("spaceName") String spaceName,
                                                  @PathParam("projectName") String projectName) {

        logger.debug("-----getBranches---, space name: {}, project name: {}",
                     spaceName,
                     projectName);

        assertObjectExists(organizationalUnitService.getOrganizationalUnit(spaceName),
                           "space",
                           spaceName);

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                spacesAPI.getSpace(spaceName),
                projectName);

        assertObjectExists(workspaceProject,
                           "project",
                           projectName);

        return workspaceProject
                .getRepository()
                .getBranches()
                .stream()
                .map(this::getBranchResponse)
                .collect(Collectors.toList());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/branches")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response addBranch(@PathParam("spaceName") String spaceName,
                              @PathParam("projectName") String projectName,
                              AddBranchRequest addBranchRequest) {

        logger.debug("-----addBranch--- , spaceName: {} , project name: {}, branch Name: {}",
                     spaceName,
                     projectName,
                     addBranchRequest.getNewBranchName());

        assertObjectExists(organizationalUnitService.getOrganizationalUnit(spaceName),
                           "space",
                           spaceName);

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                spacesAPI.getSpace(spaceName),
                projectName);

        assertObjectExists(workspaceProject,
                           "project",
                           projectName);

        final String id = newId();
        final AddBranchJobRequest jobRequest = new AddBranchJobRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setProjectName(projectName);
        jobRequest.setNewBranchName(addBranchRequest.getNewBranchName());
        jobRequest.setBaseBranchName(addBranchRequest.getBaseBranchName());
        jobRequest.setUserIdentifier(sessionInfo.getIdentity().getIdentifier());
        addAcceptedJobResult(id);

        jobRequestObserver.addBranchRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/branches/{branchName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response removeBranch(@PathParam("spaceName") String spaceName,
                                 @PathParam("projectName") String projectName,
                                 @PathParam("branchName") String branchName) {

        logger.debug("-----removeBranch--- , spaceName: {} , project name: {}, branch Name: {}",
                     spaceName,
                     projectName,
                     branchName);

        assertObjectExists(organizationalUnitService.getOrganizationalUnit(spaceName),
                           "space",
                           spaceName);

        final WorkspaceProject workspaceProject = workspaceProjectService.resolveProject(
                spacesAPI.getSpace(spaceName),
                projectName);

        assertObjectExists(workspaceProject,
                           "project",
                           projectName);

        final String id = newId();
        final RemoveBranchJobRequest jobRequest = new RemoveBranchJobRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setProjectName(projectName);
        jobRequest.setBranchName(branchName);
        jobRequest.setUserIdentifier(sessionInfo.getIdentity().getIdentifier());
        addAcceptedJobResult(id);

        jobRequestObserver.removeBranchRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    private BranchResponse getBranchResponse(Branch branch) {
        final BranchResponse branchResponse = new BranchResponse();
        branchResponse.setName(branch.getName());
        return branchResponse;
    }

    private ProjectResponse getProjectResponse(WorkspaceProject workspaceProject) {
        final ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setName(workspaceProject.getName());
        projectResponse.setSpaceName(workspaceProject.getOrganizationalUnit().getName());

        if (workspaceProject.getMainModule() != null) {
            projectResponse.setGroupId(workspaceProject.getMainModule().getPom().getGav().getGroupId());
            projectResponse.setVersion(workspaceProject.getMainModule().getPom().getGav().getVersion());
            projectResponse.setDescription(workspaceProject.getMainModule().getPom().getDescription());
        }

        final ArrayList<org.guvnor.rest.client.PublicURI> publicURIs = new ArrayList<>();

        for (PublicURI publicURI : workspaceProject.getRepository().getPublicURIs()) {
            final org.guvnor.rest.client.PublicURI responseURI = new org.guvnor.rest.client.PublicURI();
            responseURI.setProtocol(publicURI.getProtocol());
            responseURI.setUri(publicURI.getURI());
            publicURIs.add(responseURI);
        }

        projectResponse.setPublicURIs(publicURIs);
        return projectResponse;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/compile")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response compileProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {

        return compileProject(spaceName,
                              projectName,
                              null);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/compile")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response compileProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName,
            @PathParam("branchName") String branchName) {

        logger.debug("-----compileProject--- , space name: {}, project name: {}, branch name: {}",
                     spaceName,
                     projectName,
                     branchName);

        final String id = newId();
        final CompileProjectRequest jobRequest = new CompileProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setBranchName(branchName);
        addAcceptedJobResult(id);

        jobRequestObserver.compileProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/install")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response installProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {

        return installProject(spaceName,
                              projectName,
                              null);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/install")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response installProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName,
            @PathParam("branchName") String branchName) {

        logger.debug("-----installProject--- , project name: {}, branch name: {}",
                     projectName,
                     branchName);

        PortablePreconditions.checkNotNull("spaceName", spaceName);
        PortablePreconditions.checkNotNull("projectName", projectName);

        final String id = newId();
        final InstallProjectRequest jobRequest = new InstallProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setProjectName(projectName);
        jobRequest.setBranchName(branchName);
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

        return testProject(spaceName,
                           projectName,
                           null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/test")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response testProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName,
            @PathParam("branchName") String branchName) {

        logger.debug("-----testProject--- , project name: {}, branch name: {}",
                     projectName,
                     branchName);

        final String id = newId();
        final TestProjectRequest jobRequest = new TestProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setBranchName(branchName);
        addAcceptedJobResult(id);

        jobRequestObserver.testProjectRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/maven/deploy")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response deployProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName) {

        return deployProject(spaceName,
                             projectName,
                             null);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/deploy")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response deployProject(
            @PathParam("spaceName") String spaceName,
            @PathParam("projectName") String projectName,
            @PathParam("branchName") String branchName) {

        logger.debug("-----deployProject--- , project name: {}, branch name: {}",
                     projectName,
                     branchName);

        final String id = newId();
        final DeployProjectRequest jobRequest = new DeployProjectRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setProjectName(projectName);
        jobRequest.setSpaceName(spaceName);
        jobRequest.setBranchName(branchName);
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
            spaces.add(getSpace(ou));
        }

        return spaces;
    }

    private Space getSpace(OrganizationalUnit ou) {
        final Space space = new Space();
        space.setName(ou.getName());
        space.setDescription(ou.getDescription());
        space.setOwner(ou.getOwner());
        space.setDefaultGroupId(ou.getDefaultGroupId());

        final List<ProjectResponse> repoNames = new ArrayList<>();
        for (WorkspaceProject workspaceProject : workspaceProjectService.getAllWorkspaceProjects(ou)) {
            repoNames.add(getProjectResponse(workspaceProject));
        }

        space.setProjects(repoNames);
        return space;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Space getSpace(@PathParam("spaceName") String spaceName) {
        logger.debug("-----getSpace ---, Space name: {}",
                     spaceName);
        final OrganizationalUnit ou = organizationalUnitService.getOrganizationalUnit(spaceName);

        assertObjectExists(ou,
                           "space",
                           spaceName);

        return getSpace(ou);
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

        final String id = newId();
        final SpaceRequest jobRequest = new SpaceRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(space.getName());
        jobRequest.setDescription(space.getDescription());
        jobRequest.setOwner(space.getOwner());
        jobRequest.setDefaultGroupId(space.getDefaultGroupId());
        addAcceptedJobResult(id);

        jobRequestObserver.createSpaceRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response updateSpace(Space space) {
        logger.debug("-----updateSpace--- , Space name: {}, Default group id : {}",
                     space.getName(),
                     space.getDefaultGroupId());

        final String id = newId();
        final SpaceRequest jobRequest = new SpaceRequest();
        jobRequest.setStatus(JobStatus.ACCEPTED);
        jobRequest.setJobId(id);
        jobRequest.setSpaceName(space.getName());
        jobRequest.setDescription(space.getDescription());
        jobRequest.setOwner(space.getOwner());
        jobRequest.setDefaultGroupId(space.getDefaultGroupId());
        addAcceptedJobResult(id);

        jobRequestObserver.updateSpaceRequest(jobRequest);

        return createAcceptedStatusResponse(jobRequest);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/spaces/{spaceName}")
    @RolesAllowed({REST_ROLE, REST_PROJECT_ROLE})
    public Response deleteSpace(@PathParam("spaceName") String spaceName) {
        logger.debug("-----deleteSpace--- , Space name: {}",
                     spaceName);

        assertObjectExists(organizationalUnitService.getOrganizationalUnit(spaceName),
                           "space",
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

    protected void assertObjectExists(final Object o,
                                      final String objectInfo,
                                      final String objectName) {
        if (o == null) {
            throw new WebApplicationException(String.format("Could not find %s with name %s.", objectInfo, objectName),
                                              Response.status(Status.NOT_FOUND).build());
        }
    }

    protected Response createAcceptedStatusResponse(final JobRequest jobRequest) {
        return Response.status(Status.ACCEPTED).entity(jobRequest).variant(defaultVariant).build();
    }

    private String newId() {
        return "" + System.currentTimeMillis() + "-" + counter.incrementAndGet();
    }
}
