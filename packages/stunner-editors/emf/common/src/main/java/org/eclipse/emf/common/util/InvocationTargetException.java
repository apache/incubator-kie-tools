/**
 * Copyright (c) 2010 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.common.util;

/**
 * A substitute for {@link java.lang.reflect.InvocationTargetException}.
 *
 */
public class InvocationTargetException extends Exception
{
  private static final long serialVersionUID = 1L;

  public InvocationTargetException()
  {
    super();
  }

  public InvocationTargetException(String message)
  {
    super(message);
  }

  public InvocationTargetException(Throwable cause)
  {
    super(cause);
  }

  public InvocationTargetException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
