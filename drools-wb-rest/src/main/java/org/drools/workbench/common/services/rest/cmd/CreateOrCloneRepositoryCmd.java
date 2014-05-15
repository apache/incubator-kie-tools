package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.workbench.common.services.shared.rest.CreateOrCloneRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;

public class CreateOrCloneRepositoryCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateOrCloneRepositoryRequest jobRequest = (CreateOrCloneRepositoryRequest) request;

        JobResult result = null;
        try { 
        result =  helper.createOrCloneRepository( jobRequest.getJobId(), jobRequest.getRepository() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "----createOrCloneRepository--- , repository name: {} [{}]", 
                    jobRequest.getRepository().getName(), status);
        }
        return result;
    }
}
