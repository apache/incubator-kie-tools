package org.kie.workbench.common.services.shared.rest;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.shared.rest.JobRequest.Status;

@Portable
public class JobResult {

    private Status status;
    private String jodId;
    private String result;
    private Date completedTime;
    
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
    public Date getCompletedTime() {
        return completedTime;
    }
    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

}
