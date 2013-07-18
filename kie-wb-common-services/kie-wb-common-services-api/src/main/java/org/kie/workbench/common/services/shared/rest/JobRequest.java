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
    private String jodId;
    private Status status;

    
    public String getJodId() {
        return jodId;
    }
    public void setJodId(String jodId) {
        this.jodId = jodId;
    }
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

}
