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
import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;

public class CloneRepositoryCmd extends AbstractJobCommand {

    public CloneRepositoryCmd(final JobRequestHelper jobRequestHelper,
                              final JobResultManager jobResultManager,
                              final Map<String, Object> context) {
        super(jobRequestHelper,
              jobResultManager,
              context);
    }

    @Override
    public JobResult internalExecute(JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper();
        CloneProjectJobRequest jobRequest = (CloneProjectJobRequest) request;

        JobResult result = null;
        try {
            result = helper.cloneProject(jobRequest.getJobId(),
                                         jobRequest.getSpaceName(),
                                         jobRequest.getCloneProjectRequest());
        } finally {
            logger.debug("----cloneProject--- , repository name: {} [{}] [{}]",
                         jobRequest.getCloneProjectRequest().getName(),
                         getJobStatus(result),
                         getResult(result));
        }
        return result;
    }

    private String getResult(JobResult result) {
        return result != null ? result.getResult() : "";
    }

    private JobStatus getJobStatus(JobResult result) {
        return result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
    }
}
