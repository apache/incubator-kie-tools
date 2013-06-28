package org.kie.workbench.common.services.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.services.shared.rest.CloneRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.CompileProjectRequest;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.InstallProjectRequest;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;


@ApplicationScoped
public class KieSessionAsyncJobRequestObserver {
    
    @Inject 
    protected ProjectResourceDispatcher projectResourceDispatcher;
    @Inject 
    protected DefaultGuvnorApprover defaultGuvnorApprover;
    @Inject
    private Event<JobResult> jobResultEvent;
    
    public void onCloneRepositoryRequest( final @Observes CloneRepositoryRequest jobRequest ) {
        if(!approveRequest(jobRequest)) {
            return;
        }
        projectResourceDispatcher.cloneRepository(jobRequest.getJodId(), jobRequest.getRepository());
    }
    
    public void onCreateProjectRequest( final @Observes CreateProjectRequest jobRequest ) {
        if(!approveRequest(jobRequest)) {
            return;
        }
        projectResourceDispatcher.createProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName());
    }

    public void onCompileProjectRequest( final @Observes CompileProjectRequest jobRequest) {
        if(!approveRequest(jobRequest)) {
            return;
        }
        projectResourceDispatcher.compileProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig());
    }
    
    public void onInstallProjectRequest( final @Observes InstallProjectRequest jobRequest) {
        if(!approveRequest(jobRequest)) {
            return;
        }
        projectResourceDispatcher.installProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig());
    }   
    
//    public void onTestProjectRequest( final @Observes TestProjectRequest jobRequest) {
//        if(!approveRequest(jobRequest)) {
//            return;
//        }
//        projectResourceDispatcher.testProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig());
//    }
    
    //Commented out for the time being, due to kssion problem.
    public boolean approveRequest(JobRequest jobRequest) {
        if(!defaultGuvnorApprover.requestApproval(jobRequest)) {
            JobResult result = new JobResult();
            result.setJodId(jobRequest.getJodId());
            result.setStatus(JobRequest.Status.DENIED);
            result.setResult("The request is denied.");
            jobResultEvent.fire(result);
            return false;           
        }
        
        return true;
    }

/*    
    public boolean approveRequest(JobRequest jobRequest) {
        return true;
    }*/
    
}
