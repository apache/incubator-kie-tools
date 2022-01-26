/**
 * Copyright (c) 2003-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.ecore.xml.type;

/**
 * Data type exception for invalid values.
 */
public class InvalidDatatypeValueException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public InvalidDatatypeValueException(String reason)
  {
    super(reason);
  }
}
