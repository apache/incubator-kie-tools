package org.kie.workbench.common.services.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.services.shared.rest.AddRepositoryToOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.CompileProjectRequest;
import org.kie.workbench.common.services.shared.rest.CreateOrCloneRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.CreateOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.DeployProjectRequest;
import org.kie.workbench.common.services.shared.rest.InstallProjectRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryFromOrganizationalUnitRequest;
import org.kie.workbench.common.services.shared.rest.RemoveRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.TestProjectRequest;

@ApplicationScoped
public class KieSessionAsyncJobRequestObserver {

    @Inject
    protected ProjectResourceDispatcher projectResourceDispatcher;
    @Inject
    protected DefaultGuvnorApprover defaultGuvnorApprover;
    @Inject
    private Event<JobResult> jobResultEvent;

    public void onCreateOrCloneRepositoryRequest( final @Observes CreateOrCloneRepositoryRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.createOrCloneRepository( jobRequest.getJodId(), jobRequest.getRepository() );
    }

    public void onRemoveRepositoryRequest( final @Observes RemoveRepositoryRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.removeRepository( jobRequest.getJodId(), jobRequest.getRepositoryName() );
    }

    public void onCreateProjectRequest( final @Observes CreateProjectRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.createProject( jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName() );
    }

    public void onCompileProjectRequest( final @Observes CompileProjectRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.compileProject( jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig() );
    }

    public void onInstallProjectRequest( final @Observes InstallProjectRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.installProject( jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig() );
    }

    public void onTestProjectRequest( final @Observes TestProjectRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.testProject( jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig() );
    }

    public void onDeployProjectRequest( final @Observes DeployProjectRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.installProject( jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig() );
    }

    public void onCreateOrganizationalUnitRequest( final @Observes CreateOrganizationalUnitRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.createOrganizationalUnit( jobRequest.getJodId(), jobRequest.getOrganizationalUnitName(), jobRequest.getOwner(), jobRequest.getRepositories() );
    }

    public void onAddRepositoryToOrganizationalUnitRequest( final @Observes AddRepositoryToOrganizationalUnitRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.addRepositoryToOrganizationalUnit( jobRequest.getJodId(), jobRequest.getOrganizationalUnitName(), jobRequest.getRepositoryName() );
    }

    public void onAddRepositoryToOrganizationalUnitRequest( final @Observes RemoveRepositoryFromOrganizationalUnitRequest jobRequest ) {
        if ( !approveRequest( jobRequest ) ) {
            return;
        }
        projectResourceDispatcher.removeRepositoryFromOrganizationalUnit( jobRequest.getJodId(), jobRequest.getOrganizationalUnitName(), jobRequest.getRepositoryName() );
    }

    //Commented out for the time being, due to kssion problem.
    public boolean approveRequest( JobRequest jobRequest ) {
        if ( !defaultGuvnorApprover.requestApproval( jobRequest ) ) {
            JobResult result = new JobResult();
            result.setJodId( jobRequest.getJodId() );
            result.setStatus( JobRequest.Status.DENIED );
            result.setResult( "The request is denied." );
            jobResultEvent.fire( result );
            return false;
        }

        return true;
    }

/*    
    public boolean approveRequest(JobRequest jobRequest) {
        return true;
    }*/

}
