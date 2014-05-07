package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.CreateOrganizationalUnitRequest;

public class CreateOrgUnitCmd extends AbstractJobCommand {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateOrganizationalUnitRequest jobRequest = (CreateOrganizationalUnitRequest) getJobRequest(ctx);

        helper.createOrganizationalUnit( jobRequest.getJobId(), jobRequest.getOrganizationalUnitName(), jobRequest.getOwner(), jobRequest.getRepositories() );

        return getEmptyResult();
    }
}
