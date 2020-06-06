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
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.async.DescriptiveRunnable;

public abstract class AbstractJobCommand implements DescriptiveRunnable {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractJobCommand.class);

    public static final String JOB_REQUEST_KEY = "JobRequest";

    protected final JobRequestHelper jobRequestHelper;

    protected final JobResultManager jobResultManager;

    protected final Map<String, Object> context;

    public AbstractJobCommand(final JobRequestHelper jobRequestHelper,
                              final JobResultManager jobResultManager,
                              final Map<String, Object> context) {
        this.jobRequestHelper = jobRequestHelper;
        this.jobResultManager = jobResultManager;
        this.context = context;
    }

    // for command implementations

    protected JobRequestHelper getHelper() throws Exception {
        return jobRequestHelper;
    }

    protected JobRequest getJobRequest() {
        JobRequest jobRequest = (JobRequest) context.get(JOB_REQUEST_KEY);
        if (jobRequest != null) {
            return jobRequest;
        }

        throw new RuntimeException("Unable to find JobRequest");
    }

    // private helper methods

    private JobResultManager getJobManager() throws Exception {
        return jobResultManager;
    }

    @Override
    public String getDescription() {
        return "Command class " + this.getClass().getName();
    }

    @Override
    public void run() {
        try {
            // approval
            JobRequest request = getJobRequest();
            JobResult result = createResult(request);

            // save job
            logger.debug("--- job {} ---, status: {}",
                         result.getJobId(),
                         result.getStatus());
            JobResultManager jobMgr = getJobManager();
            result.setLastModified(System.currentTimeMillis());
            jobMgr.putJob(result);

            // if approved, process
            if (JobStatus.APPROVED.equals(request.getStatus())) {
                try {
                    result = internalExecute(request);
                } catch (Exception e) {
                    result.setStatus(JobStatus.SERVER_ERROR);
                    result.setResult("Request failed because of " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    logger.error("{} [{}] failed because of thrown {}: {}",
                                 request.getClass().getSimpleName(),
                                 request.getJobId(),
                                 e.getClass().getSimpleName(),
                                 e.getMessage(),
                                 e);
                }

                // save job
                logger.debug("--- job {} ---, status: {}",
                             result.getJobId(),
                             result.getStatus());
                result.setLastModified(System.currentTimeMillis());
                jobMgr.putJob(result);
            }
        } catch (Throwable e) {
            logger.error("Error executing job class: {}, error: {}",
                         this.getClass().getName(),
                         e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private JobResult createResult(JobRequest jobRequest) {
        final JobResult jobResult = new JobResult();
        jobResult.setJobId(jobRequest.getJobId());
        jobResult.setStatus(jobRequest.getStatus());
        return jobResult;
    }

    protected abstract JobResult internalExecute(JobRequest request) throws Exception;
}


