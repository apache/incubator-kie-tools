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
import java.util.Date;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@XmlRootElement(name="processInstance")
public class ProcessInstanceRef
{
  private String id;
  private String definitionId;

  private String key;

  /**
   * the active state of an instance
   */
  public static enum STATE {RUNNING, SUSPENDED, ENDED};

  /**
   * the end state of an instance
   */
  public static enum RESULT {COMPLETED, FAILED, ERROR, EXITED, OBSOLETE};
  
  private Date startDate;
  private Date endDate;

  private boolean suspended;
  private RESULT endResult;

  private transient Lifecycle lifecycle;

  private TokenReference rootToken;

  public ProcessInstanceRef()
  {
    initLifecycle();
  }

  public ProcessInstanceRef(String id, String processDefinitionId, Date startDate, Date endDate, boolean suspended)
  {

    if(null==startDate)
      throw new IllegalArgumentException("An instance requires a start date");

    if(endDate!=null && suspended)
      throw new IllegalArgumentException("An instance cannot be ended and suspended at the same time");

    this.id = id;
    this.definitionId = processDefinitionId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.suspended = suspended;
    initLifecycle();
  }

  /**
   * If not ENDED or SUSPENDED the instance is RUNNING
   */
  private void initLifecycle()
  {
    if(hasEnded())
      this.lifecycle = new Lifecycle(this, STATE.ENDED);
    else if(isSuspended())
      this.lifecycle = new Lifecycle(this, STATE.SUSPENDED);
    else
      this.lifecycle = new Lifecycle(this, STATE.RUNNING);
  }

  @XmlElement(name = "instanceId")
  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  @XmlElement(name = "definitionId")
  public String getDefinitionId()
  {
    return definitionId;
  }

  public void setDefinitionId(String definitionId)
  {
    this.definitionId = definitionId;
  }

  @XmlElement(name = "key")
  public String getKey()
  {
    return key !=null ? key : "";
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  @XmlElement(name = "status")
  public STATE getState()
  {
    return this.lifecycle.getState();
  }

  public void setState(String nextState)
  {
    setState(STATE.valueOf(nextState));
  }

  public void setState(STATE nextState)
  {
    this.lifecycle = this.lifecycle.transitionTo(nextState);
  }

  @XmlElement(name = "start")
  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  @XmlElement(name = "end")
  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public boolean isRunning()
  {
    return this.startDate!=null && !isSuspended();
  }

  public boolean hasEnded()
  {
    return this.startDate!=null
        && this.endDate!=null;
  }

  public boolean isSuspended()
  {
    return null==this.endDate && suspended;
  }

  private class Lifecycle
  {
    private STATE current;
    private ProcessInstanceRef instance;

    public Lifecycle(ProcessInstanceRef instance, STATE current)
    {
      this.instance = instance;
      this.current = current;
    }

    public Lifecycle transitionTo(STATE next)
    {
      Lifecycle nextLifecycle = null;

      switch(next)
      {
        case SUSPENDED: // only RUNNING instances can be SUSPENDED
          if(STATE.RUNNING.equals(current))
          {
            nextLifecycle = new Lifecycle(instance, next);
            instance.suspended = true;
            break;
          }
          else
          {
            throw new IllegalTransitionException(current, next);
          }
        case ENDED: // both RUNNING and SUSPENDED instances can be ENDED
          if(STATE.RUNNING.equals(current) || STATE.SUSPENDED.equals(current))
          {
            nextLifecycle =  new Lifecycle(instance, next);
            instance.suspended = false;
            instance.endDate = new Date();            
            break;
          }
          else
          {
            throw new IllegalTransitionException(current, next);
          }
        case RUNNING: // only SUSPENDED instances can become RUNNING
          if(STATE.SUSPENDED.equals(current))
          {
            nextLifecycle =  new Lifecycle(instance, next);
            instance.suspended = false;
            break;
          }
          else
          {
            throw new IllegalTransitionException(current, next);
          }
        default:
          throw new IllegalTransitionException(current, next);
      }

      return nextLifecycle;
    }

    public STATE getState()
    {
      return current;
    }


    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Lifecycle lifecycle = (Lifecycle) o;

      if (current != lifecycle.current) return false;

      return true;
    }

    public int hashCode()
    {
      int result;
      result = (current != null ? current.hashCode() : 0);
      return result;
    }
  }

  private class IllegalTransitionException extends IllegalArgumentException
  {

    public IllegalTransitionException(STATE current, STATE next)
    {
      super("Illegal transition current " + current + " next " + next);
    }
  }

  public TokenReference getRootToken()
  {
    return rootToken;
  }

  public void setRootToken(TokenReference rootToken)
  {
    this.rootToken = rootToken;
  }

  // it's actually just used for unmarshalling, TODO: fix it
  public void setSuspended(boolean suspended)
  {
    this.suspended = suspended;
    initLifecycle();
  }

  public RESULT getEndResult()
  {
    return endResult;
  }

  public void setEndResult(RESULT endResult)
  {
    if(getState()!= STATE.ENDED)
      throw new IllegalArgumentException("Cannot set end result in state "+getState());
    
    this.endResult = endResult;
  }

  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProcessInstanceRef that = (ProcessInstanceRef) o;

    if (definitionId != null ? !definitionId.equals(that.definitionId) : that.definitionId != null) return false;
    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    if (key != null ? !key.equals(that.key) : that.key != null) return false;

    return true;
  }

  public int hashCode()
  {
    int result;
    result = (id != null ? id.hashCode() : 0);
    result = 31 * result + (definitionId != null ? definitionId.hashCode() : 0);
    result = 31 * result + (key != null ? key.hashCode() : 0);
    return result;
  }
}
