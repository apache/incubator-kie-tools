package org.drools.workbench.common.services.rest.cmd;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.drools.workbench.common.services.rest.JobRequestApprovalService;
import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.drools.workbench.common.services.rest.JobResultManager;
import org.jbpm.executor.cdi.CDIUtils;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.kie.workbench.common.services.shared.rest.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJobCommand implements Command {

    protected static final Logger logger = LoggerFactory.getLogger( AbstractJobCommand.class );
            
    public static final String JOB_REQUEST_KEY = "JobRequest";

    // for command implementations
    
    protected JobRequestHelper getHelper(CommandContext ctx) throws Exception {
        BeanManager beanManager = getBeanManager();
        return CDIUtils.createBean(JobRequestHelper.class, beanManager);
    }

    protected JobRequest getJobRequest(CommandContext ctx) {
        return (JobRequest) ctx.getData(JOB_REQUEST_KEY);
    }

    protected ExecutionResults getEmptyResult() {
        return new ExecutionResults();
    }
    
    // private helper methods 
    
    private JobRequestApprovalService getApprovalService(CommandContext ctx) throws Exception {
        BeanManager beanManager = getBeanManager();
        return CDIUtils.createBean(JobRequestApprovalService.class, beanManager);
    }
       
    private JobResultManager getJobManager(CommandContext ctx) throws Exception {
        BeanManager beanManager = getBeanManager();
        return CDIUtils.createBean(JobResultManager.class, beanManager);
    }
   
    private BeanManager getBeanManager() { 
        return BeanManagerProvider.getInstance().getBeanManager();
    }
    
    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        // approval
        JobRequestApprovalService approvalService = getApprovalService(ctx);
        JobRequest request = getJobRequest(ctx);
        JobResult result = approvalService.requestApproval(request);
       
        // save job
        logger.debug( "--- job {} ---, status: {}",  result.getJobId(), result.getStatus());
        JobResultManager jobMgr = getJobManager(ctx);
        result.setLastModified(System.currentTimeMillis());
        jobMgr.putJob(result);
       
        // if approved, process
        if( JobStatus.APPROVED.equals(result.getStatus()) ) { 
            try { 
                result = internalExecute(ctx, request); 
            } catch( Exception e ) { 
                result.setStatus(JobStatus.SERVER_ERROR);
                result.setResult("Request failed because of " + e.getClass().getSimpleName() + ": " + e.getMessage());
                logger.error("{} [{}] failed because of thrown {}: {}", 
                        request.getClass().getSimpleName(), request.getJobId(), 
                        e.getClass().getSimpleName(), e.getMessage(), e);
            }

            // save job
            logger.debug( "--- job {} ---, status: {}",  result.getJobId(), result.getStatus());
            result.setLastModified(System.currentTimeMillis());
            jobMgr.putJob(result);
        }
            
        return getEmptyResult();
    }
    
    protected abstract JobResult internalExecute(CommandContext ctx, JobRequest request) throws Exception;
   
}


