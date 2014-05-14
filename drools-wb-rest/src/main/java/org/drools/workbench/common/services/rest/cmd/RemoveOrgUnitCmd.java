package org.drools.workbench.common.services.rest.cmd;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.CommandContext;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;
import org.kie.workbench.common.services.shared.rest.RemoveOrganizationalUnitRequest;

public class RemoveOrgUnitCmd extends AbstractJobCommand {

    @Override
    public JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception {
        JobRequestHelper helper = getHelper(ctx);
        RemoveOrganizationalUnitRequest jobRequest = (RemoveOrganizationalUnitRequest) request;

        JobResult result = null;
        try { 
            result = helper.removeOrganizationalUnit(jobRequest.getJobId(), jobRequest.getOrganizationalUnitName());
        } finally { 
            JobStatus status = result != null ? result.getStatus() : JobStatus.SERVER_ERROR;
            logger.debug( "-----removeOrganizationalUnit--- , OrganizationalUnit name: {}",
                    jobRequest.getOrganizationalUnitName(), status);
        }
        return result;
    }
}
