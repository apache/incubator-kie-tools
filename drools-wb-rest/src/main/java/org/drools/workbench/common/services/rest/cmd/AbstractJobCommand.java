package org.drools.workbench.common.services.rest.cmd;

import javax.enterprise.inject.spi.BeanManager;

import org.drools.workbench.common.services.rest.JobRequestHelper;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.jbpm.executor.cdi.*;
import org.kie.internal.executor.api.ExecutionResults;
import org.kie.workbench.common.services.shared.rest.JobRequest;

public abstract class AbstractJobCommand implements Command {

    public static final String JOB_REQUEST_KEY = "JobRequest";

    protected JobRequestHelper getHelper(CommandContext ctx) throws Exception {

        BeanManager beanManager = CDIUtils.lookUpBeanManager(ctx);

        return CDIUtils.createBean(JobRequestHelper.class, beanManager);
    }

    protected JobRequest getJobRequest(CommandContext ctx) {

        return (JobRequest) ctx.getData(JOB_REQUEST_KEY);

    }

    protected ExecutionResults getEmptyResult() {
        return new ExecutionResults();
    }
}


