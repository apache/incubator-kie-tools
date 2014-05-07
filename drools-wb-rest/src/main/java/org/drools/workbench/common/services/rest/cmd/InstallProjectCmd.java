package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.CompileProjectRequest;
import org.kie.workbench.common.services.shared.rest.InstallProjectRequest;

public class InstallProjectCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        InstallProjectRequest jobRequest = (InstallProjectRequest) getJobRequest(ctx);

        helper.installProject( jobRequest.getJobId(), jobRequest.getRepositoryName(), jobRequest.getProjectName() );

        return getEmptyResult();
    }
}
