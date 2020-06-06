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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.rest.backend.cmd.AddBranchCmd;
import org.guvnor.rest.backend.cmd.AbstractJobCommand;
import org.guvnor.rest.backend.cmd.AddProjectToSpaceCmd;
import org.guvnor.rest.backend.cmd.CloneRepositoryCmd;
import org.guvnor.rest.backend.cmd.CompileProjectCmd;
import org.guvnor.rest.backend.cmd.CreateProjectCmd;
import org.guvnor.rest.backend.cmd.CreateSpaceCmd;
import org.guvnor.rest.backend.cmd.DeleteProjectCmd;
import org.guvnor.rest.backend.cmd.DeployProjectCmd;
import org.guvnor.rest.backend.cmd.InstallProjectCmd;
import org.guvnor.rest.backend.cmd.RemoveSpaceCmd;
import org.guvnor.rest.backend.cmd.RemoveBranchCmd;
import org.guvnor.rest.backend.cmd.TestProjectCmd;
import org.guvnor.rest.backend.cmd.UpdateSpaceCmd;
import org.guvnor.rest.client.AddBranchJobRequest;
import org.guvnor.rest.client.AddProjectToSpaceRequest;
import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.RemoveBranchJobRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.SpaceRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.concurrent.Unmanaged;

import static org.guvnor.rest.backend.cmd.AbstractJobCommand.JOB_REQUEST_KEY;

/**
 * Utility class observing requests for various functions of the REST service
 */
@ApplicationScoped
public class JobRequestScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobRequestScheduler.class);

    private ExecutorService executorService;

    private JobResultManager jobResultManager;

    private JobRequestHelper jobRequestHelper;

    public JobRequestScheduler() {

    }

    /*
    https://issues.jboss.org/browse/AF-1587
    Workbench Rest API - deleting space sometimes randomly takes more than 60 seconds

    There's a significative improvement on timeouts by using an @Unmanaged ExecutorService.
    The @Unmanaged tends to be more available and likely that it won't share the Async execution
    of the operations itself.
    */
    @Inject
    public JobRequestScheduler(@Unmanaged ExecutorService executorService,
                               JobResultManager jobResultManager,
                               JobRequestHelper jobRequestHelper) {
        this.executorService = executorService;
        this.jobResultManager = jobResultManager;
        this.jobRequestHelper = jobRequestHelper;
    }

    public void cloneProjectRequest(final CloneProjectJobRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Space",
                   jobRequest.getCloneProjectRequest().getName());
        params.put("Operation",
                   "cloneProject");

        scheduleJob(jobRequest,
                    new CloneRepositoryCmd(jobRequestHelper,
                                           jobResultManager,
                                           params));
    }

    public void createProjectRequest(final CreateProjectJobRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Space",
                   jobRequest.getSpaceName());
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Operation",
                   "createProject");

        scheduleJob(jobRequest,
                    new CreateProjectCmd(jobRequestHelper,
                                         jobResultManager,
                                         params));
    }

    public void deleteProjectRequest(final DeleteProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Operation",
                   "deleteProject");

        scheduleJob(jobRequest,
                    new DeleteProjectCmd(jobRequestHelper,
                                         jobResultManager,
                                         params));
    }

    public void compileProjectRequest(final CompileProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Branch",
                   jobRequest.getBranchName());
        params.put("Operation",
                   "compileProject");

        scheduleJob(jobRequest,
                    new CompileProjectCmd(jobRequestHelper,
                                          jobResultManager,
                                          params));
    }

    public void installProjectRequest(final InstallProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Branch",
                   jobRequest.getBranchName());
        params.put("Operation",
                   "installProject");

        scheduleJob(jobRequest,
                    new InstallProjectCmd(jobRequestHelper,
                                          jobResultManager,
                                          params));
    }

    public void testProjectRequest(final TestProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Branch",
                   jobRequest.getBranchName());
        params.put("Operation",
                   "testProject");

        scheduleJob(jobRequest,
                    new TestProjectCmd(jobRequestHelper,
                                       jobResultManager,
                                       params));
    }

    public void deployProjectRequest(final DeployProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Branch",
                   jobRequest.getBranchName());
        params.put("Operation",
                   "deployProject");

        scheduleJob(jobRequest,
                    new DeployProjectCmd(jobRequestHelper,
                                         jobResultManager,
                                         params));
    }

    public void createSpaceRequest(final SpaceRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Operation",
                   "createOrgUnit");

        scheduleJob(jobRequest,
                    new CreateSpaceCmd(jobRequestHelper,
                                       jobResultManager,
                                       params));
    }

    public void updateSpaceRequest(final SpaceRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Operation",
                   "updateOrgUnit");

        scheduleJob(jobRequest,
                    new UpdateSpaceCmd(jobRequestHelper,
                                       jobResultManager,
                                       params));
    }

    public void addProjectToSpace(final AddProjectToSpaceRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Operation",
                   "addRepositoryToOrgUnit");

        scheduleJob(jobRequest,
                    new AddProjectToSpaceCmd(jobRequestHelper,
                                             jobResultManager,
                                             params));
    }

    public void addBranchRequest(final AddBranchJobRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Space",
                   jobRequest.getSpaceName());
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("NewBranchName",
                   jobRequest.getNewBranchName());
        params.put("BaseBranchName",
                   jobRequest.getBaseBranchName());
        params.put("Operation",
                   "addBranch");

        scheduleJob(jobRequest,
                    new AddBranchCmd(jobRequestHelper,
                                     jobResultManager,
                                     params));
    }

    public void removeBranchRequest(final RemoveBranchJobRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Space",
                   jobRequest.getSpaceName());
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("BranchName",
                   jobRequest.getBranchName());
        params.put("Operation",
                   "addBranch");

        scheduleJob(jobRequest,
                    new RemoveBranchCmd(jobRequestHelper,
                                        jobResultManager,
                                        params));
    }

    public void removeSpaceRequest(final RemoveSpaceRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Operation",
                   "removeOrgUnit");

        scheduleJob(jobRequest,
                    new RemoveSpaceCmd(jobRequestHelper,
                                       jobResultManager,
                                       params));
    }

    protected Map<String, Object> getContext(JobRequest jobRequest) {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(JOB_REQUEST_KEY,
                   jobRequest);
        params.put("BusinessKey",
                   jobRequest.getJobId());
        params.put("Retries",
                   0);
        return params;
    }

    private void scheduleJob(final JobRequest jobRequest,
                             final AbstractJobCommand command) {
        jobRequest.setStatus(JobStatus.APPROVED);
        logger.debug("Scheduling job request with id: {} and command class: {}",
                     jobRequest.getJobId(),
                     command.getClass().getName());
        this.executorService.execute(command);
    }
}
