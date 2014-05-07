package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryRequest;

public class RemoveRepositoryCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        RemoveRepositoryRequest jobRequest = (RemoveRepositoryRequest) getJobRequest(ctx);

        helper.removeRepository( jobRequest.getJobId(), jobRequest.getRepositoryName() );

        return getEmptyResult();
    }
}
