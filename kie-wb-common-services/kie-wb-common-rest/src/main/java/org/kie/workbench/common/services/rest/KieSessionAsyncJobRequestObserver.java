package org.kie.workbench.common.services.rest;


import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.services.shared.rest.CloneRepositoryRequest;
import org.kie.workbench.common.services.shared.rest.CompileProjectRequest;
import org.kie.workbench.common.services.shared.rest.CreateProjectRequest;
import org.kie.workbench.common.services.shared.rest.InstallProjectRequest;
import org.kie.workbench.common.services.shared.rest.TestProjectRequest;


@ApplicationScoped
public class KieSessionAsyncJobRequestObserver {
    
    @Inject 
    protected ProjectResourceDispatcher projectResourceDispatcher;
    
    public void onCloneRepositoryRequest( final @Observes CloneRepositoryRequest jobRequest ) {
        projectResourceDispatcher.cloneRepository(jobRequest.getJodId(), jobRequest.getRepository());
    }
    
    public void onCreateProjectRequest( final @Observes CreateProjectRequest jobRequest ) {
        projectResourceDispatcher.createProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName());
    }

    public void onCompileProjectRequest( final @Observes CompileProjectRequest jobRequest) {
        projectResourceDispatcher.compileProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig());
    }
    
    public void onInstallProjectRequest( final @Observes InstallProjectRequest jobRequest) {
        projectResourceDispatcher.installProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig());
    }   
    
    public void onTestProjectRequest( final @Observes TestProjectRequest jobRequest) {
        projectResourceDispatcher.testProject(jobRequest.getJodId(), jobRequest.getRepositoryName(), jobRequest.getProjectName(), jobRequest.getBuildConfig());
    }  

}
