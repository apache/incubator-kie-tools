/**
 * Copyright (c) 2005-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.util;

import java.io.PrintStream;


/**
 * The most basic implementation of a task monitor.
 */
public class BasicMonitor implements Monitor 
{
  private boolean isCanceled;
  
  private Diagnostic blockedReason;
  
  public BasicMonitor()
  {
    super();
  }

  public boolean isCanceled()
  {
    return isCanceled;
  }

  public void setCanceled(boolean isCanceled)
  {
    this.isCanceled = isCanceled;
  }
  
  /**
   * Returns the current reason for task being blocked, or <code>null</code>.
   */
  public Diagnostic getBlockedReason()
  {
    return blockedReason;
  }
  
  public void setBlocked(Diagnostic reason)
  {
    this.blockedReason = reason;
  }

  public void clearBlocked()
  {
    this.blockedReason = null;
  }
 
  public void beginTask(String name, int totalWork)
  {
    // Do nothing.
  }
  
  public void setTaskName(String name)
  {
    // Do nothing.
  }

  public void subTask(String name)
  {
    // Do nothing.
  }

  public void worked(int work)
  {
    // Do nothing.
  }
  
  public void internalWorked(double work)
  {
    // Do nothing.
  }
    
  public void done()
  {
    // Do nothing.
  }
  
  /**
   * A simple monitor that delegates to another monitor.
   */
  public static class Delegating implements Monitor
  {
    protected Monitor monitor;
    
    public Delegating(Monitor monitor)
    {
      this.monitor = monitor; 
    }
    
    public boolean isCanceled()
    {
      return monitor.isCanceled();
    }

    public void setCanceled(boolean value)
    {
      monitor.setCanceled(value);
    }

    public void setBlocked(Diagnostic reason)
    {
      monitor.setBlocked(reason);
    }
    
    public void clearBlocked()
    {
      monitor.clearBlocked();
    }

    public void beginTask(String name, int totalWork)
    {
      monitor.beginTask(name, totalWork);
    }

    public void setTaskName(String name)
    {
      monitor.setTaskName(name);
    }

    public void subTask(String name)
    {
      monitor.subTask(name);
    }

    public void worked(int work)
    {
      monitor.worked(work);
    }
    
    public void internalWorked(double work)
    {
      monitor.internalWorked(work);
    }

    public void done()
    {
      monitor.done();
    }
  }

  /**
   * A simple monitor that prints progress to a print stream.
   */
  public static class Printing extends BasicMonitor
  {
    protected PrintStream printStream;

    public Printing(PrintStream printStream)
    {
      this.printStream = printStream;
    }

    @Override
    public void beginTask(String name, int totalWork)
    {
      if (name != null && name.length() != 0)
      {
        printStream.println(">>> " + name);
      }
    }

    @Override
    public void setTaskName(String name)
    {
      if (name != null && name.length() != 0)
      {
        printStream.println("<>> " + name);
      }
    }

    @Override
    public void subTask(String name)
    {
      if (name != null && name.length() != 0)
      {
        printStream.println(">>  " + name);
      }
    }
    
    @Override
    public void setBlocked(Diagnostic reason)
    {
      super.setBlocked(reason);
      printStream.println("#>  " + reason.getMessage());
    }
  
    @Override
    public void clearBlocked()
    {
      printStream.println("=>  " + getBlockedReason().getMessage());
      super.clearBlocked();
    }
  }
}
