package org.jboss.bpm.console.client.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "wrapper")
public class HistoryActivityInstanceRefWrapper {
  List<HistoryActivityInstanceRef> historyEntires;

  public HistoryActivityInstanceRefWrapper()
  {
  }

  public HistoryActivityInstanceRefWrapper(List<HistoryActivityInstanceRef> historyEntires)
  {
     this.historyEntires = historyEntires;
  }

  @XmlElement
  public List<HistoryActivityInstanceRef> getDefinitions()
  {
     return historyEntires;
  }

  @XmlElement(name = "totalCount")
  public int getTotalCount()
  {
     return historyEntires.size();
  }

  public void setDefinitions(List<HistoryActivityInstanceRef> historyEntires)
  {
     this.historyEntires = historyEntires;
  }
}
