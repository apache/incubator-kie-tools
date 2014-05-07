/*
* Copyright 2013 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.drools.workbench.common.services.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.common.services.rest.cmd.AddRepositoryToOrgUnitCmd;
import org.drools.workbench.common.services.rest.cmd.CompileProjectCmd;
import org.drools.workbench.common.services.rest.cmd.CreateOrCloneRepositoryCmd;
import org.drools.workbench.common.services.rest.cmd.CreateOrgUnitCmd;
import org.drools.workbench.common.services.rest.cmd.CreateProjectCmd;
import org.drools.workbench.common.services.rest.cmd.DeployProjectCmd;
import org.drools.workbench.common.services.rest.cmd.InstallProjectCmd;
import org.drools.workbench.common.services.rest.cmd.RemoveRepositoryCmd;
import org.drools.workbench.common.services.rest.cmd.RemoveRepositoryFromOrgUnitCmd;
import org.drools.workbench.common.services.rest.cmd.TestProjectCmd;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.workbench.common.services.shared.rest.AddRepositoryToOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.CompileProjectRequest;
import org.kie.workbench.common.services.shared.rest.CreateOrCloneRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.CreateOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.DeployProjectRequest;
import org.kie.workbench.common.services.shared.rest.InstallProjectRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryFromOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.TestProjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.workbench.common.services.rest.cmd.AbstractJobCommand.*;

/**
 * Utility class observing requests for various functions of the REST service
 */
@ApplicationScoped
public class JobRequestObserver {

    private static final Logger logger = LoggerFactory.getLogger( JobRequestObserver.class );

    @Inject
    protected JobRequestApprovalService approvalService;

    @Inject
    private Event<JobResult> jobResultEvent;

    @Inject
    private ExecutorService executorService;

    public void onCreateOrCloneRepositoryRequest( final @Observes CreateOrCloneRepositoryRequest jobRequest ) {
        logger.info( "CreateOrCloneRepositoryRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "CreateOrCloneRepositoryRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(CreateOrCloneRepositoryCmd.class.getName(), getContext(jobRequest));
    }

    public void onRemoveRepositoryRequest( final @Observes RemoveRepositoryRequest jobRequest ) {
        logger.info( "RemoveRepositoryRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "RemoveRepositoryRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(RemoveRepositoryCmd.class.getName(), getContext(jobRequest));
    }

    public void onCreateProjectRequest( final @Observes CreateProjectRequest jobRequest ) {
        logger.info( "CreateProjectRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "CreateProjectRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(CreateProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void onCompileProjectRequest( final @Observes CompileProjectRequest jobRequest ) {
        logger.info( "CompileProjectRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "CompileProjectRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(CompileProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void onInstallProjectRequest( final @Observes InstallProjectRequest jobRequest ) {
        logger.info( "InstallProjectRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "InstallProjectRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(InstallProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void onTestProjectRequest( final @Observes TestProjectRequest jobRequest ) {
        logger.info( "TestProjectRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "TestProjectRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(TestProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void onDeployProjectRequest( final @Observes DeployProjectRequest jobRequest ) {
        logger.info( "DeployProjectRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "DeployProjectRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(DeployProjectCmd.class.getName(), getContext(jobRequest));
    }

    public void onCreateOrganizationalUnitRequest( final @Observes CreateOrganizationalUnitRequest jobRequest ) {
        logger.info( "CreateOrganizationalUnitRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "CreateOrganizationalUnitRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(CreateOrgUnitCmd.class.getName(), getContext(jobRequest));
    }

    public void onAddRepositoryToOrganizationalUnitRequest( final @Observes AddRepositoryToOrganizationalUnitRequest jobRequest ) {
        logger.info( "AddRepositoryToOrganizationalUnitRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "AddRepositoryToOrganizationalUnitRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(AddRepositoryToOrgUnitCmd.class.getName(), getContext(jobRequest));
    }

    public void onAddRepositoryToOrganizationalUnitRequest( final @Observes RemoveRepositoryFromOrganizationalUnitRequest jobRequest ) {
        logger.info( "RemoveRepositoryFromOrganizationalUnitRequest event received." );
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        logger.info( "RemoveRepositoryFromOrganizationalUnitRequest event approved. Performing requested operation." );
        executorService.scheduleRequest(RemoveRepositoryFromOrgUnitCmd.class.getName(), getContext(jobRequest));
    }

    private boolean approveRequest( final JobRequest jobRequest ) {
        final JobResult result = approvalService.requestApproval( jobRequest );
        return result.getStatus().equals( JobStatus.APPROVED );
    }

    protected CommandContext getContext(JobRequest jobRequest) {
        CommandContext ctx = new CommandContext();
        ctx.setData(JOB_REQUEST_KEY, jobRequest);
        ctx.setData("retries", 0);

        return ctx;
    }

}
