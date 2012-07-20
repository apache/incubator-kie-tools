package org.jboss.bpm.console.client.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;



@XmlRootElement(name = "historyProcessInstance")
@Portable
public class HistoryActivityInstanceRef implements Serializable
{

  private String activityName;
  private Date startTime;
  private Date endTime;
  private String executionId;
  private long duration;
  
  public String getActivityName() {
    return activityName;
  }
  
  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }
  
  public Date getStartTime() {
    return startTime;
  }
  
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }
  
  public Date getEndTime() {
    return endTime;
  }
  
  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
  
  public String getExecutionId() {
    return executionId;
  }
  
  public void setExecutionId(String executionId) {
    this.executionId = executionId;
  }
  
  public long getDuration() {
    return duration;
  }
  
  public void setDuration(long duration) {
    this.duration = duration;
  }
  
  
}
