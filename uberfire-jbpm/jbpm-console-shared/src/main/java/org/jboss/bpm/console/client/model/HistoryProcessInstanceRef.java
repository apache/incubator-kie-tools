package org.jboss.bpm.console.client.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement(name = "historyProcessInstance")
@Portable
public class HistoryProcessInstanceRef implements Serializable
{

  
  private String processInstanceId;
  private String processDefinitionId;
  private String key;
  private String state;
  private String endActivityName;
  private Date startTime;
  private Date endTime;
  private long duration;
  
  public String getProcessInstanceId() {
    return processInstanceId;
  }
  
  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }
  
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }
  
  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }
  
  public String getKey() {
    return key;
  }
  
  public void setKey(String key) {
    this.key = key;
  }
  
  public String getState() {
    return state;
  }
  
  public void setState(String state) {
    this.state = state;
  }
  
  public String getEndActivityName() {
    return endActivityName;
  }
  
  public void setEndActivityName(String endActivityName) {
    this.endActivityName = endActivityName;
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
  
  public long getDuration() {
    return duration;
  }
  
  public void setDuration(long duration) {
    this.duration = duration;
  }
  
  
}
