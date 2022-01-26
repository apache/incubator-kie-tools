/**
 * Copyright (c) 2008-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.command;

/**
 * An exception thrown when a command's {@link Command#execute() execution} is to be silently aborted.
 * This is a signal to the command stack to behave as if {@link Command#canExecute() canExecute} returned <code>false</code>.
 * Only a command that has not changed the state of the model should be aborted in this way.
 */
public class AbortExecutionException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  /**
   * Constructs an new instance.
   */
  public AbortExecutionException()
  {
    super();
  }

  /**
   * Constructs a new instance with the given message and cause.
   * @param message a description of the reason for aborting.
   * @param cause an indication of why execution was aborted.
   */
  public AbortExecutionException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Constructs a new instance with the given message.
   * @param message a description of the reason for aborting.
   */
  public AbortExecutionException(String message)
  {
    super(message);
  }

  /**
   * Constructs a new instance with the given message and cause.
   * @param cause an indication of why execution was aborted.
   */
  public AbortExecutionException(Throwable cause)
  {
    super(cause);
  }
}
