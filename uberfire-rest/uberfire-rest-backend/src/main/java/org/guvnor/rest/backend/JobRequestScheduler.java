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

import org.guvnor.rest.backend.cmd.AbstractJobCommand;
import org.guvnor.rest.backend.cmd.AddRepositoryToOrgUnitCmd;
import org.guvnor.rest.backend.cmd.CompileProjectCmd;
import org.guvnor.rest.backend.cmd.CreateOrCloneRepositoryCmd;
import org.guvnor.rest.backend.cmd.CreateOrgUnitCmd;
import org.guvnor.rest.backend.cmd.CreateProjectCmd;
import org.guvnor.rest.backend.cmd.DeleteProjectCmd;
import org.guvnor.rest.backend.cmd.DeployProjectCmd;
import org.guvnor.rest.backend.cmd.InstallProjectCmd;
import org.guvnor.rest.backend.cmd.RemoveOrgUnitCmd;
import org.guvnor.rest.backend.cmd.RemoveRepositoryCmd;
import org.guvnor.rest.backend.cmd.RemoveRepositoryFromOrgUnitCmd;
import org.guvnor.rest.backend.cmd.TestProjectCmd;
import org.guvnor.rest.backend.cmd.UpdateOrgUnitCmd;
import org.guvnor.rest.client.AddRepositoryToOrganizationalUnitRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.RemoveOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.guvnor.rest.client.UpdateOrganizationalUnitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.concurrent.Managed;

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

    @Inject
    public JobRequestScheduler(@Managed ExecutorService executorService,
                               JobResultManager jobResultManager,
                               JobRequestHelper jobRequestHelper) {
        this.executorService = executorService;
        this.jobResultManager = jobResultManager;
        this.jobRequestHelper = jobRequestHelper;
    }

    public void createOrCloneRepositoryRequest(final CreateOrCloneRepositoryRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepository().getName());
        params.put("Operation",
                   "createOrCloneRepository");

        scheduleJob(jobRequest,
                    new CreateOrCloneRepositoryCmd(jobRequestHelper,
                                                   jobResultManager,
                                                   params));
    }

    public void removeRepositoryRequest(final RemoveRepositoryRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepositoryName());
        params.put("Operation",
                   "removeRepository");

        scheduleJob(jobRequest,
                    new RemoveRepositoryCmd(jobRequestHelper,
                                            jobResultManager,
                                            params));
    }

    public void createProjectRequest(final CreateProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepositoryName());
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
        params.put("Repository",
                   jobRequest.getRepositoryName());
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
        params.put("Repository",
                   jobRequest.getRepositoryName());
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Operation",
                   "compileProject");

        scheduleJob(jobRequest,
                    new CompileProjectCmd(jobRequestHelper,
                                          jobResultManager,
                                          params));
    }

    public void installProjectRequest(final InstallProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepositoryName());
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Operation",
                   "installProject");

        scheduleJob(jobRequest,
                    new InstallProjectCmd(jobRequestHelper,
                                          jobResultManager,
                                          params));
    }

    public void testProjectRequest(final TestProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepositoryName());
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Operation",
                   "testProject");

        scheduleJob(jobRequest,
                    new TestProjectCmd(jobRequestHelper,
                                       jobResultManager,
                                       params));
    }

    public void deployProjectRequest(final DeployProjectRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepositoryName());
        params.put("Project",
                   jobRequest.getProjectName());
        params.put("Operation",
                   "deployProject");

        scheduleJob(jobRequest,
                    new DeployProjectCmd(jobRequestHelper,
                                         jobResultManager,
                                         params));
    }

    public void createOrganizationalUnitRequest(final CreateOrganizationalUnitRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Operation",
                   "createOrgUnit");

        scheduleJob(jobRequest,
                    new CreateOrgUnitCmd(jobRequestHelper,
                                         jobResultManager,
                                         params));
    }

    public void updateOrganizationalUnitRequest(final UpdateOrganizationalUnitRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Operation",
                   "updateOrgUnit");

        scheduleJob(jobRequest,
                    new UpdateOrgUnitCmd(jobRequestHelper,
                                         jobResultManager,
                                         params));
    }

    public void addRepositoryToOrganizationalUnitRequest(final AddRepositoryToOrganizationalUnitRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepositoryName());
        params.put("Operation",
                   "addRepositoryToOrgUnit");

        scheduleJob(jobRequest,
                    new AddRepositoryToOrgUnitCmd(jobRequestHelper,
                                                  jobResultManager,
                                                  params));
    }

    public void removeRepositoryFromOrganizationalUnitRequest(final RemoveRepositoryFromOrganizationalUnitRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Repository",
                   jobRequest.getRepositoryName());
        params.put("Operation",
                   "removeRepositoryFromOrgUnit");

        scheduleJob(jobRequest,
                    new RemoveRepositoryFromOrgUnitCmd(jobRequestHelper,
                                                       jobResultManager,
                                                       params));
    }

    public void removeOrganizationalUnitRequest(final RemoveOrganizationalUnitRequest jobRequest) {
        final Map<String, Object> params = getContext(jobRequest);
        params.put("Operation",
                   "removeOrgUnit");

        scheduleJob(jobRequest,
                    new RemoveOrgUnitCmd(jobRequestHelper,
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
