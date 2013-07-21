package org.kie.workbench.common.services.shared.rest;

import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.shared.rest.JobRequest.Status;

@Portable
public class JobResult {

    private Status status;
    private String jodId;
    private String result;
    private long lastModified;
    private List<String> detailedResult;
    
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public String getJodId() {
        return jodId;
    }
    public void setJodId(String jodId) {
        this.jodId = jodId;
    }
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
    public long getLastModified() {
        return lastModified;
    }
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
	public List<String> getDetailedResult() {
		return detailedResult;
	}
	public void setDetailedResult(List<String> detailedResult) {
		this.detailedResult = detailedResult;
	}
}
