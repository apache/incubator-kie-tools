/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.rest.backend.cmd;

import java.util.Map;

import org.guvnor.rest.backend.JobRequestHelper;
import org.guvnor.rest.backend.JobResultManager;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;

public class DeployProjectCmd extends AbstractJobCommand {

    public DeployProjectCmd(final JobRequestHelper jobRequestHelper,
                            final JobResultManager jobResultManager,
                            final Map<String, Object> context) {
        super(jobRequestHelper,
              jobResultManager,
              context);
    }

    @Override
    public JobResult internalExecute(JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper();
        DeployProjectRequest jobRequest = (DeployProjectRequest) request;

        JobResult result = null;
        try {
            result = helper.deployProject(jobRequest.getJobId(),
                                          jobRequest.getSpaceName(),
                                          jobRequest.getProjectName(),
                                          jobRequest.getBranchName());
        } finally {
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug("-----deployProject--- , project name: {}, branch name: {} [{}]",
                         jobRequest.getProjectName(),
                         jobRequest.getBranchName(),
                         status);
        }
        return result;
    }
}
