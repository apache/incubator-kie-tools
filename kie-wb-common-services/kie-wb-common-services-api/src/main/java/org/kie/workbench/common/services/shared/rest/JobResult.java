package org.kie.workbench.common.services.shared.rest;

import java.util.Date;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.shared.rest.JobRequest.Status;

@Portable
public class JobResult {

    private Status status;
    private String jobId;
    private String result;
    private long lastModified;
    private List<String> detailedResult;
    
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
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
