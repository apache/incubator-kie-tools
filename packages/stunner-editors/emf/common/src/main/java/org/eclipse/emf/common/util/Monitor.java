/**
 * Copyright (c) 2005-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.util;


/**
 * An task monitor that provides control and feedback.
 */
public interface Monitor 
{
  /**
   * Returns whether the activity has been canceled.
   */
  boolean isCanceled();
  
  /**
   * Sets whether the active should be canceled.
   */
  void setCanceled(boolean value);
  
  /**
   * Sets the reason for the activity being blocked.
   */
  void setBlocked(Diagnostic reason);
  
  /**
   * Clears the reason for the activity being blocked.
   */
  void clearBlocked();

  /**
   * Represents an unknown amount or work.
   */
  int UNKNOWN = -1;
  
  /**
   * Called once per instance to indicate the name of the task and its expected duration.
   */
  void beginTask(String name, int totalWork);
  
  /**
   * Update the task name.
   */
  void setTaskName(String name);
  
  /**
   * Sets the current subtask of the overall task.
   */
  void subTask(String name);

  /**
   * Called to indicate the amount or progress on the task.
   */
  void worked(int work);
  
  /**
   * Called by subprogress monitors to do fractional work.
   */
  void internalWorked(double work);
    
  /**
   * Called to indicate the task is complete.
   *
   */
  void done();
}

