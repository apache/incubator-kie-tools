package org.jboss.bpm.console.client.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "wrapper")
public class HistoryProcessInstanceRefWrapper {
  List<HistoryProcessInstanceRef> historyEntires;

  public HistoryProcessInstanceRefWrapper()
  {
  }

  public HistoryProcessInstanceRefWrapper(List<HistoryProcessInstanceRef> historyEntires)
  {
     this.historyEntires = historyEntires;
  }

  @XmlElement
  public List<HistoryProcessInstanceRef> getDefinitions()
  {
     return historyEntires;
  }

  @XmlElement(name = "totalCount")
  public int getTotalCount()
  {
     return historyEntires.size();
  }

  public void setDefinitions(List<HistoryProcessInstanceRef> historyEntires)
  {
     this.historyEntires = historyEntires;
  }

}
