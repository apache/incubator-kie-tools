package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;

public class CreateProjectCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateProjectRequest jobRequest = (CreateProjectRequest) request;

        JobResult result = null;
        try { 
            result= helper.createProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----createProject--- , repositoryName: {}, project name: {} [{}]", 
                    jobRequest.getRepositoryName(), jobRequest.getProjectName(), status);
        }
        return result;
    }
}
