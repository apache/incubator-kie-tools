package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.TestProjectRequest;

public class TestProjectCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        TestProjectRequest jobRequest = (TestProjectRequest) getJobRequest(ctx);

        helper.testProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig() );

        return getEmptyResult();
    }
}
