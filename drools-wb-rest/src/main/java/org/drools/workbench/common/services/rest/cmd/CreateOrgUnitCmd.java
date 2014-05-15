package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.workbench.common.services.shared.rest.CreateOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;

public class CreateOrgUnitCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        CreateOrganizationalUnitRequest jobRequest = (CreateOrganizationalUnitRequest) request;

        JobResult result = null;
        try { 
            result = helper.createOrganizationalUnit( jobRequest.getJobId(), jobRequest.getOrganizationalUnitName(), jobRequest.getOwner(), jobRequest.getRepositories() );
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----createOrganizationalUnit--- , OrganizationalUnit name: {}, OrganizationalUnit owner: {} [{}]",
                    jobRequest.getOrganizationalUnitName(), jobRequest.getOwner(), status );
        }
        return result;
    }
}
