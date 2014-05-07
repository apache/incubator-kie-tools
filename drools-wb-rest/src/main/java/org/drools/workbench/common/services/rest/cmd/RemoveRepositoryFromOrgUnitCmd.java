package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryFromOrganizationalUnitRequest;

public class RemoveRepositoryFromOrgUnitCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        RemoveRepositoryFromOrganizationalUnitRequest jobRequest = (RemoveRepositoryFromOrganizationalUnitRequest) getJobRequest(ctx);

        helper.removeRepositoryFromOrganizationalUnit( jobRequest.getJobId(), jobRequest.getOrganizationalUnitName(), jobRequest.getRepositoryName() );

        return getEmptyResult();
    }
}
