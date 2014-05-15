package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;
import org.kie.workbench.common.services.shared.rest.TestProjectRequest;

public class TestProjectCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        TestProjectRequest jobRequest = (TestProjectRequest) request;

        JobResult result = null;
        try { 
            result = helper.testProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----testProject--- , repositoryName: {}, project name: {} [{}]",
                    jobRequest.getRepositoryName(), jobRequest.getProjectName(), status);
        }
        return result;
    }
}
