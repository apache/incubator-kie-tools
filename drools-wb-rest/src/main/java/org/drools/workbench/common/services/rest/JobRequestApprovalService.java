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
import javax.inject.Inject;

import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.workbench.common.services.shared.rest.JobRequest;
import org.kie.workbench.common.services.shared.rest.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class to approve requests for jobs
 */
@ApplicationScoped
public class JobRequestApprovalService {

    private static final Logger logger = LoggerFactory.getLogger( JobRequestApprovalService.class );

//    @Inject
//    @KSession("ksession1")
    KieSession ksession = null;

    public JobResult requestApproval( final JobRequest jobRequest ) {
        logger.info( "Approval request for Job: " + jobRequest.getJobId() + " received." );
        final JobResult jobResult = new JobResult();
        jobResult.setJobId( jobRequest.getJobId() );
        jobResult.setStatus( jobRequest.getStatus() );

        //If no ksession is available default to true
        if ( ksession == null ) {
            return jobResult;
        }

        //Delegate approval to ksession
        FactHandle fhJobRequest = null;
        FactHandle fhJobResult = null;
        try {
            fhJobRequest = ksession.insert( jobRequest );
            fhJobResult = ksession.insert( jobResult );
            ksession.fireAllRules();
        } finally {
            if ( fhJobRequest != null ) {
                ksession.delete( fhJobRequest );
            }
            if ( fhJobResult != null ) {
                ksession.delete( fhJobResult );
            }
        }
        logger.info( "Approval request for Job: " + jobRequest.getJobId() + " result: " + jobRequest.getStatus() );
        return jobResult;
    }

}
