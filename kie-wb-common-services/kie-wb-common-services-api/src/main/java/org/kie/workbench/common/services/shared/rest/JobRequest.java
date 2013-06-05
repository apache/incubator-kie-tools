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
        FAIL
    }
    private String jodId;

    private Status status;
    private String result;
    
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
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

}
