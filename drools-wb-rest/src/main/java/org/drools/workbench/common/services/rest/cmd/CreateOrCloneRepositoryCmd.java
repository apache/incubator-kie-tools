package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.CreateOrCloneRepositoryRequest;

public class CreateOrCloneRepositoryCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateOrCloneRepositoryRequest jobRequest = (CreateOrCloneRepositoryRequest) getJobRequest(ctx);

        helper.createOrCloneRepository( jobRequest.getJobId(), jobRequest.getRepository() );

        return getEmptyResult();
    }
}
