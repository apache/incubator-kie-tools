/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.console.client.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@XmlRootElement(name="processDefinition")
public class ProcessDefinitionRef 
{
  private String id;
  
  private String name;
  private long version;
  private String key;
  private String description;
  private String packageName;

  private String deploymentId;
  private boolean suspended;
  private String formUrl = null;
  private String diagramUrl = null;

  public ProcessDefinitionRef()
  {
  }

  public ProcessDefinitionRef(String id, String name, long version)
  {
    this.id = id;
    this.name = name;
    this.version = version;
  }

  @XmlElement(name = "processId")
  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  @XmlElement(name = "name")
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public long getVersion()
  {
    return version;
  }

  public void setVersion(long version)
  {
    this.version = version;
  }

  public String toString()
  {
    return "ProcessDefinitionRef{id="+this.id +", name="+this.name+", version="+this.version+"}";
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getPackageName()
  {
    return packageName;
  }

  public void setPackageName(String packageName)
  {
    this.packageName = packageName;
  }

  public String getDeploymentId()
  {
    return deploymentId;
  }

  public void setDeploymentId(String deploymentId)
  {
    this.deploymentId = deploymentId;
  }

  public boolean isSuspended()
  {
    return suspended;
  }

  public void setSuspended(boolean suspended)
  {
    this.suspended = suspended;
  }

  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProcessDefinitionRef that = (ProcessDefinitionRef) o;

    if (version != that.version) return false;
    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    if (key != null ? !key.equals(that.key) : that.key != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;

    return true;
  }

  public int hashCode()
  {
    int result;
    result = (id != null ? id.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (int) (version ^ (version >>> 32));
    result = 31 * result + (key != null ? key.hashCode() : 0);
    return result;
  }

  public void setFormUrl(String s)
  {
    this.formUrl = s;
  }

  public String getFormUrl()
  {
    return formUrl;
  }

  public String getDiagramUrl()
  {
    return diagramUrl;
  }

  public void setDiagramUrl(String diagramUrl)
  {
    this.diagramUrl = diagramUrl;
  }
}
