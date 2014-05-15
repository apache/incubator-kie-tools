package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryRequest;

public class RemoveRepositoryCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        RemoveRepositoryRequest jobRequest = (RemoveRepositoryRequest) request;

        JobResult result = null;
        try { 
            result = helper.removeRepository( jobRequest.getJobId(), jobRequest.getRepositoryName() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.info( "-----removeRepository--- , repository name: {} [{}]", 
                    jobRequest.getRepositoryName(), status);
        }
        return result;
    }
}
