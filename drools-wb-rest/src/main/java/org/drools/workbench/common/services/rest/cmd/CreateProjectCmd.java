package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryRequest;

public class CreateProjectCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateProjectRequest jobRequest = (CreateProjectRequest) getJobRequest(ctx);

        helper.createProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName() );

        return getEmptyResult();
    }
}
