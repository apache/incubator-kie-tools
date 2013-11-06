package org.kie.workbench.common.services.shared.rest;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class JobRequest {
    public enum Status {
        GONE,
        ACCEPTED,
        BAD_REQUEST,
        RESOURCE_NOT_EXIST,
        DUPLICATE_RESOURCE,
        SERVER_ERROR,
        SUCCESS,
        FAIL,
        DENIED
    }
    private String jobId;
    private Status status;

    
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jodId) {
        this.jobId = jodId;
    }
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

}
