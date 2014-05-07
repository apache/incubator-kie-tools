package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.DeployProjectRequest;

public class DeployProjectCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        DeployProjectRequest jobRequest = (DeployProjectRequest) getJobRequest(ctx);

        helper.deployProject(jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName());

        return getEmptyResult();
    }
}
